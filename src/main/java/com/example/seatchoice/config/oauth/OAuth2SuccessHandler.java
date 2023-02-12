package com.example.seatchoice.config.oauth;

import static org.springframework.http.HttpStatus.OK;

import com.example.seatchoice.config.jwt.TokenService;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final TokenService tokenService;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String accessToken = tokenService.createToken(oAuth2User).getAccessToken();
		String refreshToken = tokenService.createRefreshToken(oAuth2User).getRefreshToken();
		setResponse(response, OK, accessToken, refreshToken);
	}

	private void setResponse(
		HttpServletResponse response, HttpStatus httpStatus, String accessToken, String refreshToken)
		throws IOException {

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(httpStatus.value());
		Cookie cookie = new Cookie("refreshToken", refreshToken);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);

		JSONObject responseJson = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("accessToken", accessToken);
		responseJson.put("data", data);
		response.getWriter().print(responseJson);
	}
}
