package com.example.seatchoice.controller;

import static org.springframework.http.HttpStatus.OK;

import com.example.seatchoice.config.jwt.TokenService;
import com.example.seatchoice.dto.auth.Token.AccessToken;
import com.example.seatchoice.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

	private final TokenService tokenService;

	@GetMapping("/api/reissue/refresh-token")
	@ResponseStatus(OK)
	public ApiResponse<AccessToken> reissueAccessToken(
		@AuthenticationPrincipal OAuth2User oAuth2User, @RequestHeader(value = "refreshToken") String refreshToken) {

		AccessToken accessToken = tokenService.reissueAccessToken(oAuth2User, refreshToken);

		return new ApiResponse(accessToken);
	}
}
