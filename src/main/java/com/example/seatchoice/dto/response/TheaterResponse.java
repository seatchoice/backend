package com.example.seatchoice.dto.response;

import com.example.seatchoice.entity.Facility;
import com.example.seatchoice.entity.Theater;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "공연장 조회 응답")
public class TheaterResponse {

	@Schema(description = "시설 id", example = "898")
	private Long facilityId;

	@Schema(description = "시설명", example = "세종예술의전당")
	private String facilityName;
	@ArraySchema(schema = @Schema(implementation = TheaterDto.class))
	private List<TheaterDto> theaterList;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Schema(name = "공연장 dto")
	public static class TheaterDto {
		@Schema(description = "공연장 id", example = "1217")
		private Long id;
		@Schema(description = "공연장명", example = "대공연장")
		private String name;
		@Schema(description = "좌석수", example = "1070")
		private int seatCnt;

		public static TheaterDto of(Theater theater) {
			return TheaterDto.builder()
				.id(theater.getId())
				.name(theater.getName())
				.seatCnt(theater.getSeatCnt())
				.build();
		}
	}

	public static TheaterResponse of(List<Theater> theaterList, Facility facility) {
		return TheaterResponse.builder()
			.facilityId(facility.getId())
			.facilityName(facility.getName())
			.theaterList(theaterList.stream()
				.map(TheaterDto::of).collect(Collectors.toList()))
			.build();
	}

}
