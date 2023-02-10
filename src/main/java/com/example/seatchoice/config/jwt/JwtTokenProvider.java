package com.example.seatchoice.config.jwt;

import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	@Value("${spring.jwt.token.secret-key}")
	private String secretKey;

	private final long TOKEN_VALID_TIME = 30 * 60 * 1000L;
	private final UserDetailsService userDetailsService;
	private final MemberRepository memberRepository;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public Token createToken(Long memberId, List<String> roles) {
		Claims claims = Jwts.claims().setSubject(String.valueOf(memberId));
		claims.put("roles", roles);
		Date now = new Date();

		String accessToken = Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + TOKEN_VALID_TIME))
			.signWith(HS256, secretKey)
			.compact();

		return new Token(accessToken);
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUserId(token));

		return new UsernamePasswordAuthenticationToken(
			userDetails, "", userDetails.getAuthorities());
	}

	public String getUserId(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest request) {
		return request.getHeader("Authorization");
	}

	public boolean validateToken(String jwtToken) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody();
		String memberId = claims.getSubject();

		memberRepository.findById(Long.valueOf(memberId))
			.orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER, NOT_FOUND));

		return claims.getExpiration().after(new Date());
	}
}
