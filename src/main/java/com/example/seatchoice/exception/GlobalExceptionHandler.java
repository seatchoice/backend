package com.example.seatchoice.exception;

import static com.example.seatchoice.type.ErrorCode.METHOD_ARGUMENT_NOT_VALID;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.example.seatchoice.dto.common.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 직접 발생시키는 예외 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		return ErrorResponse.from(e.getErrorCode(), e.getHttpStatus());
	}

	// 발생되는 예외 예시, 밑으로 추가
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		return ErrorResponse.from(METHOD_ARGUMENT_NOT_VALID, BAD_REQUEST);
	}
}
