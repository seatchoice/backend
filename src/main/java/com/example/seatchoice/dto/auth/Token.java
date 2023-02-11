package com.example.seatchoice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Token {

	@Getter
	@AllArgsConstructor
	public static class AccessToken {
		private String accessToken;
	}

	@Getter
	@AllArgsConstructor
	public static class RefreshToken {
		private String refreshToken;
	}
}
