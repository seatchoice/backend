package com.example.seatchoice.controller.elasticsearch;

import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.entity.document.FacilityDoc;
import com.example.seatchoice.service.elasticsearch.FacilityDocService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FacilityDocController {

	private final FacilityDocService facilityDocService;

	@GetMapping("/search/facility")
	public ApiResponse<List<FacilityDoc>> searchFacility(@RequestParam String name,
		@RequestParam(required = false) Long after, @RequestParam int size) {

		return new ApiResponse<>(facilityDocService.searchFacility(name, after, size));
	}

	@PostMapping("/save/facility")
	public ApiResponse<Void> saveFacilityDoc(){

		facilityDocService.saveFacilities();

		return new ApiResponse<>();
	}

}
