package com.example.seatchoice.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 발생되는 에러 exception명 그대로 작성
	METHOD_ARGUMENT_NOT_VALID("유효성 검사 실패"),

	// 직접 발생시키는 에러 밑으로 추가
	EXPIRED_TOKEN("만료된 토큰입니다."),
	INVALID_TOKEN("유효하지 않은 토큰입니다."),
	NOT_FOUND_MEMBER("해당 유저가 존재하지 않습니다.");

	private final String message;
}
