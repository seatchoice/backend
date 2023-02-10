package com.example.seatchoice.controller.elasticsearch;

import com.example.seatchoice.service.elasticsearch.PerformanceDocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PerformanceDocController {

	private final PerformanceDocService performanceDocService;

	@PostMapping("/save/performance")
	public ResponseEntity<Void> savePerformanceDoc(){
		performanceDocService.savePerformances();
		return ResponseEntity.ok().build();
	}

}