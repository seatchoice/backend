package com.example.seatchoice.service.oauth;

import static com.example.seatchoice.type.ErrorCode.AUTHORIZATION_KEY_DOES_NOT_EXIST;
import static com.example.seatchoice.type.ErrorCode.EXPIRED_REFRESH_TOKEN;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.example.seatchoice.dto.auth.Token.AccessToken;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
		Object id = member.getId();
		Object nickname = member.getNickname();

		Map<String, Object> claims = new HashMap<>();
		claims.put("id", id);
		claims.put("nickname", nickname);

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
		Object id = member.getId();
		Object nickname = member.getNickname();

		Map<String, Object> claims = new HashMap<>();
		claims.put("id", id);
		claims.put("nickname", nickname);

		Date now = new Date();

		String refreshToken =  Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + REFRESH_PERIOD))
			.signWith(SignatureAlgorithm.HS256, refreshSecretKey)
			.compact();

		// redis refreshToken 저장
		Long memberId = Long.valueOf(id.toString());
		HashOperations<Long, Object, Object> hashOperations = redisTemplate.opsForHash();
		hashOperations.put(memberId, REDIS_REFRESH_TOKEN_KEY, refreshToken);
		redisTemplate.expire(memberId, REFRESH_PERIOD, MILLISECONDS);

		return refreshToken;
	}

	public Authentication getAuthentication(String token) {
		Long memberId = getMemberId(token);

		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new CustomException(NOT_FOUND_MEMBER, NOT_FOUND));

		Map<String, Object> memberInfo = new HashMap<>();
		memberInfo.put("id", memberId);
		memberInfo.put("nickname", getNickname(token));

		OAuth2User oAuth2User = new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())), memberInfo, "id");

		return new UsernamePasswordAuthenticationToken(
			oAuth2User, "", oAuth2User.getAuthorities());
	}

	public String resolveToken(HttpServletRequest request) {
		String accessToken = request.getHeader("Authorization");

		if (accessToken == null) {
			throw new CustomException(AUTHORIZATION_KEY_DOES_NOT_EXIST, BAD_REQUEST);
		}

		return accessToken;
	}

	public boolean validateToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		return claims.getExpiration().after(new Date());
	}

	public AccessToken reissueAccessToken(HttpServletRequest request, String refreshToken) {
		String token = resolveToken(request);
		Long memberId = getMemberId(token);
		String redisRefreshToken = redisTemplate.opsForHash().get(memberId, REDIS_REFRESH_TOKEN_KEY).toString();

		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new CustomException(NOT_FOUND_MEMBER, NOT_FOUND));

		if (!redisRefreshToken.equals(refreshToken)) {
			throw new CustomException(EXPIRED_REFRESH_TOKEN, UNAUTHORIZED);
		}

		String accessToken = createToken(member);

		return new AccessToken(accessToken);
	}

	public Long getMemberId(String token) {
		String memberId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("id").toString();
		return Long.valueOf(memberId);
	}

	public Object getNickname(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("nickname");
	}
}
