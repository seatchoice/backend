package com.example.seatchoice.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginType {

	KAKAO("kakao");

	private final String provider;
}
