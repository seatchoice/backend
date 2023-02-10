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
	NOT_FOUND_MEMBER("해당 유저가 존재하지 않습니다."),
	NOT_FOUND_THEATER("해당 공연장이 존재하지 않습니다."),
	NOT_FOUND_CHATROOM("해당 채팅방이 존재하지 않습니다."),
	NOT_FOUND_SEAT("해당 공연좌석이 존재하지 않습니다."),
	IMAGE_UPLOAD_FAIL("이미지 업로드에 실패했습니다."),
	WRONG_FILE_FORM("잘못된 형식의 파일입니다."),
	ERROR_CODE_500("서버 에러. 문의가 필요합니다.")
	;

	private final String message;
}
