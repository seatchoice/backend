package com.example.seatchoice.config;

import static com.example.seatchoice.type.ErrorCode.EMPTY_TOKEN;
import static com.example.seatchoice.type.ErrorCode.EXPIRED_TOKEN;
import static com.example.seatchoice.type.ErrorCode.INVALID_TOKEN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.service.oauth.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

	private final TokenService tokenService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		try {
			String token = tokenService.resolveToken((HttpServletRequest) request);

			if (tokenService.validateToken(token)) {
				Authentication authentication = tokenService.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		} catch (CustomException e) {
				request.setAttribute("errorCode", e.getErrorCode());
				request.setAttribute("httpStatus", e.getHttpStatus());
		} catch (SignatureException | MalformedJwtException e) {
			request.setAttribute("errorCode", INVALID_TOKEN);
			request.setAttribute("httpStatus", UNAUTHORIZED);
		} catch (ExpiredJwtException e) {
			request.setAttribute("errorCode", EXPIRED_TOKEN);
			request.setAttribute("httpStatus", UNAUTHORIZED);
		} catch (IllegalArgumentException e) {
			request.setAttribute("errorCode", EMPTY_TOKEN);
			request.setAttribute("httpStatus", BAD_REQUEST);
		}

		chain.doFilter(request, response);
	}
}
