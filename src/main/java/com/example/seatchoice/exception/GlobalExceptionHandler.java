package com.example.seatchoice.exception;

import static com.example.seatchoice.type.ErrorCode.ERROR_CODE_500;
import static com.example.seatchoice.type.ErrorCode.METHOD_ARGUMENT_NOT_VALID;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.example.seatchoice.dto.common.ErrorResponse;
import com.example.seatchoice.dto.validation.ValidErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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
	public ResponseEntity<ValidErrorResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		return ValidErrorResponse.from(METHOD_ARGUMENT_NOT_VALID, e.getFieldError().getField(),
			e.getFieldError().getDefaultMessage(), BAD_REQUEST);
	}

	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleServerErrorException(Exception e) {
		return ErrorResponse.from(ERROR_CODE_500, INTERNAL_SERVER_ERROR);
	}
}
