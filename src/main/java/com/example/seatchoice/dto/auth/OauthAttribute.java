package com.example.seatchoice.dto.auth;

import com.example.seatchoice.type.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OauthAttribute {

	private String oauthId;
	private String email;
	private String nickname;
	private LoginType loginType;
}
