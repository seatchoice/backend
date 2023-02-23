package com.example.seatchoice.controller;

import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.service.MemberService;
import com.example.seatchoice.service.auth.TokenService;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MemberController {

	private final MemberService memberService;
	private final TokenService tokenService;

	private final String REFRESH_TOKEN_KEY = "refreshToken";

	@PostMapping("/oauth/{provider}/login")
	public ResponseEntity<Void>  oauthLogin(
		@RequestParam String code, @PathVariable("provider") String provider, HttpServletResponse response)
		throws IOException, ParseException {

		Token token = memberService.oauthLogin(code, provider);
		response.setHeader("Authorization", token.getAccessToken());

		Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, token.getRefreshToken());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/oauth/{provider}/logout")
	public ResponseEntity<Void> oauthLogout(
		@AuthenticationPrincipal Member member, @PathVariable("provider") String provider, HttpServletResponse response)
		throws IOException {

		memberService.oauthLogout(member.getId(), provider);
		tokenService.resetHeader(response);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/auth/tokens")
	public ResponseEntity<Void> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = tokenService.reissueAccessToken(request, response);
		response.setHeader("Authorization", accessToken);

		return ResponseEntity.ok().build();
	}
}
