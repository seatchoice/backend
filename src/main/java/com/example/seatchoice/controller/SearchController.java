package com.example.seatchoice.controller;

import com.example.seatchoice.service.elasticsearch.FacilityDocService;
import com.example.seatchoice.service.elasticsearch.PerformanceDocService;
import com.example.seatchoice.type.SearchType;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

	private final PerformanceDocService performanceDocService;
	private final FacilityDocService facilityDocService;

	@GetMapping
	public ResponseEntity<?> searchFacilityOrPerformance(
		@RequestParam(defaultValue = "FACILITY") SearchType type,
		@RequestParam(defaultValue = "") String name,
		@RequestParam(required = false) Long after,
		@RequestParam(defaultValue = "30") int size,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyyMMdd") Date startDate,
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyyMMdd") Date endDate,
		@RequestParam(required = false) String sido,
		@RequestParam(required = false) String gugun
	) {
		if (type == SearchType.PERFORMANE && sido == null && gugun == null) {
			return ResponseEntity.ok(performanceDocService
				.searchPerformance(name, after, size, startDate, endDate));
		}

		if (type == SearchType.FACILITY && startDate == null && endDate == null) {
			return ResponseEntity.ok(facilityDocService
				.searchFacility(name, after, size, sido, gugun));
		}

		return ResponseEntity.badRequest().body("type별 파라미터를 확인하세요.");
	}

}
