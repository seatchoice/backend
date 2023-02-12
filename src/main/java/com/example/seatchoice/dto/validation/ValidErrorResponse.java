package com.example.seatchoice.dto.validation;

import com.example.seatchoice.dto.common.ErrorResponse;
import com.example.seatchoice.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor
public class ValidErrorResponse {
	private ErrorCode errorCode;
	private String errorField;
	private String errorMessage;

	public static ResponseEntity<ValidErrorResponse> from(ErrorCode errorCode,
		String errorField,
		String errorMessage,
		HttpStatus status) {
		return ResponseEntity
			.status(status)
			.body(ValidErrorResponse.builder()
				.errorCode(errorCode)
				.errorField(errorField)
				.errorMessage(errorMessage)
				.build());
	}
}
