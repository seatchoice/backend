package com.example.seatchoice.controller;

import com.example.seatchoice.dto.common.ErrorResponse;
import com.example.seatchoice.dto.response.TheaterResponse;
import com.example.seatchoice.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/theaters")
@Tag(name = "theater", description = "공연장 API")
public class TheaterController {
	private final TheaterService theaterService;

	@Tag(name = "theater")
	@Operation(
		summary = "공연장을 조회합니다.",
		description = "선택한 시설의 모든 공연장을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "공연장 조회 성공",
			content = @Content(
				array = @ArraySchema(schema = @Schema(implementation = TheaterResponse.class))
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "공연장 조회 실패 - notfound",
			content = @Content(
				schema = @Schema(implementation = ErrorResponse.class),
				examples = {
					@ExampleObject(name = "존재하지 않는 시설", value = " {\n    \"errorCode\": \"NOT_FOUND_FACILITY\",\n    \"errorMessage\": \"해당 시설이 존재하지 않습니다.\"\n  }")
				}
			)
		)
	})
	@GetMapping(value = "/{facilityId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TheaterResponse> getListOfFacility(
		@Parameter(description = "[시설 id]", example = "17")
		@PathVariable Long facilityId) {

		return ResponseEntity.ok(theaterService.getListOfFacility(facilityId));
	}

}
