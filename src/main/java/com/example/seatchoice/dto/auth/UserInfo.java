package com.example.seatchoice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfo {

	private String oauthId;
	private String nickname;
	private String email;
}
