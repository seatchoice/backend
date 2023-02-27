package com.example.seatchoice.controller;

import static com.example.seatchoice.type.ErrorCode.NOT_TYPE_REQUEST_PARAMETER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.example.seatchoice.entity.document.FacilityDoc;
import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.service.elasticsearch.FacilityDocService;
import com.example.seatchoice.service.elasticsearch.PerformanceDocService;
import com.example.seatchoice.type.SearchType;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

	private final PerformanceDocService performanceDocService;
	private final FacilityDocService facilityDocService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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

		if (type == SearchType.PERFORMANCE && sido == null && gugun == null) {
			log.info("type은 공연");
			List<PerformanceDoc> performanceDocs = performanceDocService
				.searchPerformance(name, after, size, startDate, endDate);
			return ResponseEntity.ok(performanceDocs);
		}

		if (type == SearchType.FACILITY && startDate == null && endDate == null) {
			log.info("type은 시설");
			List<FacilityDoc> facilityDocs = facilityDocService
				.searchFacility(name, after, size, sido, gugun);
			return ResponseEntity.ok(facilityDocs);
		}

		throw new CustomException(NOT_TYPE_REQUEST_PARAMETER, BAD_REQUEST);
	}
}
