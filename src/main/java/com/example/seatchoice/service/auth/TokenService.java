package com.example.seatchoice.service.auth;

import static com.example.seatchoice.type.ErrorCode.AUTHORIZATION_KEY_DOES_NOT_EXIST;
import static com.example.seatchoice.type.ErrorCode.EXPIRED_REFRESH_TOKEN;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService{

	@Value("${spring.jwt.token.secret-key")
	private String secretKey;

	@Value("${spring.jwt.token.refresh-secret-key")
	private String refreshSecretKey;

	private final MemberRepository memberRepository;
	private final RedisTemplate<Long, Object> redisTemplate;
	private final long TOKEN_PERIOD = 30 * 60 * 1000L;
	private final long REFRESH_PERIOD = 14 * 24 * 60 * 60 * 1000L;
	private final String REDIS_REFRESH_TOKEN_KEY = "refreshToken";

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
		refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
	}

	public String createToken(Member member) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", member.getId());
		claims.put("nickname", member.getNickname());
		Date now = new Date();

		String accessToken = Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + TOKEN_PERIOD))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();


		return accessToken;
	}

	public String createRefreshToken(Member member) {
		Long memberId = member.getId();
		Claims claims = Jwts.claims().setSubject(String.valueOf(memberId));
		Date now = new Date();

		String refreshToken =  Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + REFRESH_PERIOD))
			.signWith(SignatureAlgorithm.HS256, refreshSecretKey)
			.compact();

		// redis refreshToken 저장
		HashOperations<Long, Object, Object> hashOperations = redisTemplate.opsForHash();
		hashOperations.put(memberId, REDIS_REFRESH_TOKEN_KEY, refreshToken);
		redisTemplate.expire(memberId, REFRESH_PERIOD, MILLISECONDS);

		return refreshToken;
	}

	public Authentication getAuthentication(String token) {
		Long memberId = getMemberId(token);

		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new CustomException(NOT_FOUND_MEMBER, NOT_FOUND));

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(member.getRole().name()));

		return new UsernamePasswordAuthenticationToken(member, "", grantedAuthorities);
	}

	public String resolveToken(HttpServletRequest request) {
		String accessToken = request.getHeader("Authorization");

		if (accessToken == null) {
			throw new CustomException(AUTHORIZATION_KEY_DOES_NOT_EXIST, BAD_REQUEST);
		}

		return accessToken;
	}

	public boolean validateToken(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
			return claims.getExpiration().after(new Date());

		} catch (ExpiredJwtException e) {
			return false;
		}
	}

	public String reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
		try {
			String refreshToken = getRefreshToken(request);
			Long memberId = getMemberIdFromRefreshToken(refreshToken);
			String redisRefreshToken = redisTemplate.opsForHash().get(memberId, REDIS_REFRESH_TOKEN_KEY).toString();

			Member member = memberRepository.findById(memberId).orElseThrow(
				() -> new CustomException(NOT_FOUND_MEMBER, NOT_FOUND));

			if (!redisRefreshToken.equals(refreshToken)) {
				resetHeader(response);
				throw new CustomException(EXPIRED_REFRESH_TOKEN, UNAUTHORIZED);
			}

			return createToken(member);

		} catch (NullPointerException e) {
			resetHeader(response);
			throw new CustomException(EXPIRED_REFRESH_TOKEN, UNAUTHORIZED);
		}
	}

	public Long getMemberId(String token) {
		String memberId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
		return Long.valueOf(memberId);
	}

	public Long getMemberIdFromRefreshToken(String refreshToken) {
		String memberId =
			Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).getBody().getSubject();
		return Long.valueOf(memberId);
	}

	private String getRefreshToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie :  cookies) {
			if (cookie.getName().equals("refreshToken")) {
				return cookie.getValue();
			}
		}
		return null;
	}

	// 만료된 access, refresh token 정보 삭제
	public void resetHeader(HttpServletResponse response) {
		response.setHeader("Authorization", null);
		Cookie cookie = new Cookie("refreshToken", null);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
}
