package com.example.seatchoice.dto.common;

import com.example.seatchoice.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponse {

	private ErrorCode errorCode;
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
