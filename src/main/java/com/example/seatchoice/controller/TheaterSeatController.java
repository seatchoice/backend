package com.example.seatchoice.controller;

import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.service.TheaterSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TheaterSeatController {

	private final TheaterSeatService theaterSeatService;

	@GetMapping("/theaters/{theaterId}/seats")
	public ApiResponse<?> getSeatsWithReviews(@PathVariable Long theaterId) {
		return new ApiResponse<>(theaterSeatService.getSeatsWithReviews(theaterId));
	}
}
