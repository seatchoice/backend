package com.example.seatchoice.controller;

import com.example.seatchoice.service.elasticsearch.PerformanceDocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performance")
public class PerformanceDocController {

	private final PerformanceDocService performanceDocService;

	@PostMapping("/save")
	public ResponseEntity<Void> savePerformanceDoc(){

		performanceDocService.savePerformances();

		return ResponseEntity.ok().build();
	}

}