package com.example.seatchoice.controller;

import com.example.seatchoice.dto.response.TheaterResponse;
import com.example.seatchoice.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/theaters")

public class TheaterController {
	private final TheaterService theaterService;

	@GetMapping("/{facilityId}")
	public ResponseEntity<TheaterResponse> getListOfFacility(@PathVariable Long facilityId) {

		return ResponseEntity.ok(theaterService.getListOfFacility(facilityId));
	}

}
