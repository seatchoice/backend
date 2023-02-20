package com.example.seatchoice.controller;

import com.example.seatchoice.dto.response.TheaterSeatResponse;
import com.example.seatchoice.service.TheaterSeatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<List<TheaterSeatResponse>> getSeatsWithReviews(@PathVariable Long theaterId) {
		return ResponseEntity.ok(theaterSeatService.getSeatsWithReviews(theaterId));
	}
}
