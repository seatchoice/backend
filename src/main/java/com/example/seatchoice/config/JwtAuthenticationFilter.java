package com.example.seatchoice.config;

import static com.example.seatchoice.type.ErrorCode.EMPTY_TOKEN;
import static com.example.seatchoice.type.ErrorCode.INVALID_TOKEN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.service.auth.TokenService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

			if (tokenService.validateToken(token)) { // access_token 유효할 때
				Authentication authentication = tokenService.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else { // access_token 유효하지 않을 때 재발급
				String accessToken =
					tokenService.reissueAccessToken((HttpServletRequest) request, (HttpServletResponse) response);
				((HttpServletResponse) response).setHeader("Authorization", accessToken);
				Authentication authentication = tokenService.getAuthentication(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		} catch (CustomException e) {
				request.setAttribute("errorCode", e.getErrorCode());
				request.setAttribute("httpStatus", e.getHttpStatus());
		} catch (SignatureException | MalformedJwtException e) {
			request.setAttribute("errorCode", INVALID_TOKEN);
			request.setAttribute("httpStatus", UNAUTHORIZED);
		} catch (IllegalArgumentException e) {
			request.setAttribute("errorCode", EMPTY_TOKEN);
			request.setAttribute("httpStatus", BAD_REQUEST);
		}

		chain.doFilter(request, response);
	}
}
