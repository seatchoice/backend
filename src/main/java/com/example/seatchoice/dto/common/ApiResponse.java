package com.example.seatchoice.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ApiResponse<T> {

	@Nullable
	private T data;
}
