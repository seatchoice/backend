package com.example.seatchoice.exception;

import com.example.seatchoice.type.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

	private ErrorCode errorCode;
	private String errorMessage;
	private HttpStatus httpStatus;

	public CustomException(ErrorCode errorCode, HttpStatus httpStatus) {
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getMessage();
		this.httpStatus = httpStatus;
	}
}
