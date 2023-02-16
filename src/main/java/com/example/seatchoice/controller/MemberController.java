package com.example.seatchoice.controller;

import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.service.MemberService;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

	private final MemberService memberService;

	private final String REFRESH_TOKEN_KEY = "refreshToken";

	@PostMapping("/api/oauth/{provider}/login")
	public ApiResponse oauthLogin(
		@RequestParam String code, @PathVariable("provider") String provider, HttpServletResponse response)
		throws IOException, ParseException {

		Token token = memberService.oauthLogin(code, provider);
		response.setHeader("Authorization", token.getAccessToken());

		Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, token.getRefreshToken());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);

		return new ApiResponse();
	}
}
