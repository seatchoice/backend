package com.example.seatchoice.controller.elasticsearch;

import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.service.elasticsearch.PerformanceDocService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PerformanceDocController {

	private final PerformanceDocService performanceDocService;

	@GetMapping("/search/performance")
	public ResponseEntity<List<PerformanceDoc>> searchPerformance(@RequestParam String name,
		@RequestParam(required = false) Long after, @RequestParam int size) {
		List<PerformanceDoc> results = performanceDocService.searchPerformance(name, after, size);
		return ResponseEntity.ok(results);
	}

	@PostMapping("/save/performance")
	public ResponseEntity<Void> savePerformanceDoc(){
		performanceDocService.savePerformances();
		return ResponseEntity.ok().build();
	}

}