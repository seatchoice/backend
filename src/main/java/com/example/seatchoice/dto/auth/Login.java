package com.example.seatchoice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Login {

	private String accessToken;
	private String refreshToken;
	private String nickname;
}
