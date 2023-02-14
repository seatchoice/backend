package com.example.seatchoice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Token {

	private String accessToken;
	private String refreshToken;

	@Getter
	@AllArgsConstructor
	public static class AccessToken {
		private String accessToken;
	}
}
