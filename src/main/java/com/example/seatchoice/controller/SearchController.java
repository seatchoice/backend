package com.example.seatchoice.controller;

import static com.example.seatchoice.type.ErrorCode.NOT_TYPE_REQUEST_PARAMETER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.example.seatchoice.dto.common.ErrorResponse;
import com.example.seatchoice.entity.document.FacilityDoc;
import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.service.elasticsearch.FacilityDocService;
import com.example.seatchoice.service.elasticsearch.PerformanceDocService;
import com.example.seatchoice.type.SearchType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "search", description = "검색 API")
public class SearchController {

	private final PerformanceDocService performanceDocService;
	private final FacilityDocService facilityDocService;

	@Tag(name = "search")
	@Operation(
		summary =  "시설/공연을 검색합니다.",
		description = "시설명 or 공연명으로 (+ 조건) 검색을 합니다."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "시설 검색 성공",
			content = @Content(examples = {
				@ExampleObject(name = "검색 유형이 시설인 예시",
					value = "[{\n"
						+ "    \"id\": 10,\n"
						+ "    \"name\": \"(재)영화의전당\",\n"
						+ "    \"totalSeatCnt\": null,\n"
						+ "    \"sido\": \"부산\",\n"
						+ "    \"gugun\": \"해운대구\",\n"
						+ "    \"address\": \"부산광역시 해운대구 수영강변대로 120 (우동)\"\n"
						+ "  }]"),
				@ExampleObject(name = "검색 유형이 공연인 예시",
					value = "[{\n"
						+ "    \"id\": 1,\n"
						+ "    \"name\": \"신림이론\",\n"
						+ "    \"startDate\": \"2023-02-10T00:00:00.000+00:00\",\n"
						+ "    \"endDate\": \"2023-02-12T00:00:00.000+00:00\",\n"
						+ "    \"poster\": \"http://www.kopis.or.kr/upload/pfmPoster/PF_PF212781_230207_164800.gif\",\n"
						+ "    \"genrenm\": \"연극\",\n"
						+ "    \"openrun\": false\n"
						+ "  }]")}
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "검색 실패 - bad requst",
			content = @Content(
				schema = @Schema(implementation = ErrorResponse.class),
				examples = {
					@ExampleObject(name = "검색 type에 맞지 않는 잘못된 요청", value = " {\n    \"errorCode\": \"NOT_TYPE_REQUEST_PARAMETER\",\n    \"errorMessage\": \"검색 type의 요청 파라미터가 아닙니다.\"\n  }")
				}
			)
		)
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> searchFacilityOrPerformance(
		@Parameter(schema = @Schema(implementation = SearchType.class),
			example = "FACILITY", description = "[검색 타입 (시설/공연)]  \n \n Default value : FACILITY")
		@RequestParam(defaultValue = "FACILITY") SearchType type,
		@Parameter(example = "예술의전당", description = "[이름 (시설/공연)]")
		@RequestParam(defaultValue = "") String name,
		@Parameter(example = "30", description = "[요청된 응답의 마지막 id (시설/공연)]  \n \n 입력한 id 다음 리스트를 조회")
		@RequestParam(required = false) Long after,
		@Parameter(example = "40", description = "[요청 리스트 사이즈 (시설/공연)]")
		@RequestParam(defaultValue = "30") int size,
		@Parameter(example = "20230201", description = "[공연 시작날짜 (공연)]  \n \n yyyyMMdd 형식으로 입력",
			schema = @Schema(type = "Date"))
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyyMMdd") Date startDate,
		@Parameter(example = "20230401", description = "[공연 종료날짜 (공연)]  \n \n yyyyMMdd 형식으로 입력",
			schema = @Schema(type = "Date"))
		@RequestParam(required = false) @DateTimeFormat(pattern = "yyyyMMdd") Date endDate,
		@Parameter(example = "서울", description = "[시설 시도 (시설)]")
		@RequestParam(required = false) String sido,
		@Parameter(example = "송파구", description = "[시설 구군 (시설)]")
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
