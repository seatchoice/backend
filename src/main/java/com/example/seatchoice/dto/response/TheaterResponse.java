package com.example.seatchoice.dto.response;

import com.example.seatchoice.entity.Facility;
import com.example.seatchoice.entity.Theater;
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
public class TheaterResponse {

	private Long facilityId;
	private String facilityName;
	private List<TheaterDto> theaterList;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TheaterDto {
		private Long id;
		private String name;
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
