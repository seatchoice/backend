package com.example.seatchoice.config.jwt;

import static com.example.seatchoice.type.ErrorCode.EXPIRED_TOKEN;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.example.seatchoice.dto.auth.Token.AccessToken;
import com.example.seatchoice.dto.auth.Token.RefreshToken;
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

	private final MemberRepository memberRepository;
	private final RedisTemplate<Long, Object> redisTemplate;
	private final long TOKEN_PERIOD = 30 * 60 * 1000L;
	private final long REFRESH_PERIOD = 6 * 60 * 60 * 1000L;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public AccessToken createToken(OAuth2User oAuth2User) {
		Object id = oAuth2User.getAttributes().get("id");
		Object nickname = oAuth2User.getAttributes().get("nickname");

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


		return new AccessToken(accessToken);
	}

	public RefreshToken createRefreshToken(OAuth2User oAuth2User) {
		Object id = oAuth2User.getAttributes().get("id");
		Object nickname = oAuth2User.getAttributes().get("nickname");

		Map<String, Object> claims = new HashMap<>();
		claims.put("id", id);
		claims.put("nickname", nickname);

		Date now = new Date();

		String refreshToken =  Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + REFRESH_PERIOD))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();

		// redis refreshToken 저장
		Long memberId = Long.valueOf(id.toString());
		HashOperations<Long, Object, Object> hashOperations = redisTemplate.opsForHash();
		hashOperations.put(memberId, "refreshToken", refreshToken);
		redisTemplate.expire(memberId, REFRESH_PERIOD, MILLISECONDS);

		return new RefreshToken(refreshToken);
	}

	public Authentication getAuthentication(String token) {
		Long memberId = getMemberId(token);

		Map<String, Object> memberInfo = new HashMap<>();
		memberInfo.put("id", memberId);
		memberInfo.put("nickname", getNickname(token));

		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new CustomException(NOT_FOUND_MEMBER, NOT_FOUND));

		OAuth2User oAuth2User = new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())), memberInfo, "id");

		return new UsernamePasswordAuthenticationToken(
			oAuth2User, "", oAuth2User.getAuthorities());
	}

	public Long getMemberId(String token) {
		String memberId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("id").toString();

		return Long.valueOf(memberId);
	}

	public Object getNickname(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("nickname");
	}

	public String resolveToken(HttpServletRequest request) {
		return request.getHeader("Authorization");
	}

	public boolean validateToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

		return claims.getExpiration().after(new Date());
	}

	public AccessToken reissueAccessToken(OAuth2User oAuth2User, String refreshToken) {
		Long memberId = Long.valueOf(oAuth2User.getAttributes().get("id").toString());
		String redisRefreshToken = redisTemplate.opsForHash().get(memberId, "refreshToken").toString();

		if (!redisRefreshToken.equals(refreshToken)) {
			throw new CustomException(EXPIRED_TOKEN, UNAUTHORIZED);
		}

		return createToken(oAuth2User);
	}
}
