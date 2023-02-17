package com.example.seatchoice.controller;

import com.example.seatchoice.service.elasticsearch.FacilityDocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/facility")
public class FacilityDocController {

	private final FacilityDocService facilityDocService;

	@PostMapping("/save")
	public ResponseEntity<Void> saveFacilityDoc(){

		facilityDocService.saveFacilities();

		return ResponseEntity.ok().build();
	}

}
