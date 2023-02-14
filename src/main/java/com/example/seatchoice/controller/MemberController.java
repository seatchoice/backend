package com.example.seatchoice.controller;

import static org.springframework.http.HttpStatus.OK;

import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.dto.auth.Token.AccessToken;
import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.service.MemberService;
import com.example.seatchoice.service.oauth.TokenService;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

	private final TokenService tokenService;
	private final MemberService memberService;
	private final RedisTemplate<Long, Object> redisTemplate;

	private final String REFRESH_TOKEN_KEY = "refreshToken";

	@PostMapping("/api/oauth/{provider}/login")
	public ApiResponse<AccessToken> oauthLogin(
		@RequestParam String code, @PathVariable("provider") String provider, HttpServletResponse response)
		throws IOException, ParseException {

		Token token = memberService.oauthLogin(code, provider);
		AccessToken accessToken = new AccessToken(token.getAccessToken());

		Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, token.getRefreshToken());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);

		return new ApiResponse(accessToken);
	}

	@GetMapping("/api/reissue/refresh-token")
	@ResponseStatus(OK)
	public ApiResponse<AccessToken> reissueAccessToken(
		HttpServletRequest request, @CookieValue String refreshToken) {
		AccessToken accessToken = tokenService.reissueAccessToken(request, refreshToken);

		return new ApiResponse(accessToken);
	}
}
