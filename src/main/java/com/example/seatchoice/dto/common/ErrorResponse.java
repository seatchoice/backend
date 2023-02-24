package com.example.seatchoice.dto.common;

import com.example.seatchoice.type.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor
@Schema(name = "에러 응답")
public class ErrorResponse {

	@Schema(description = "에러 코드", example = "NOT_FOUND_MEMBER")
	private ErrorCode errorCode;
	@Schema(description = "에러 메시지", example = "해당 유저가 존재하지 않습니다.")
	private String errorMessage;

	public static ResponseEntity<ErrorResponse> from(ErrorCode errorCode, HttpStatus status) {
		return ResponseEntity
			.status(status)
			.body(ErrorResponse.builder()
				.errorCode(errorCode)
				.errorMessage(errorCode.getMessage())
				.build());
	}
}
