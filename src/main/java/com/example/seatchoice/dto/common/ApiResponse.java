package com.example.seatchoice.dto.common;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

	@Nullable
	private T data;
}
