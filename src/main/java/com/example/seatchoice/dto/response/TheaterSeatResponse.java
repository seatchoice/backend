package com.example.seatchoice.dto.response;

import com.example.seatchoice.entity.TheaterSeat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheaterSeatResponse {

	private Integer floor;
	private List<Section> sections;

	public static TheaterSeatResponse from(Integer floor, List<Section> sections) {
		return TheaterSeatResponse.builder()
			.floor(floor)
			.sections(sections)
			.build();
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Section {

		private String section;
		private List<Seat> seats;

		public static Section from(String section, List<Seat> seats) {
			return Section.builder()
				.section(section)
				.seats(seats)
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Seat {

		private Long seatId;
		private String seatRow;
		private Integer seatNumber;
		private Long reviewAmount;
		private Double rating;

		private static Seat from(TheaterSeat theaterSeat) {
			return Seat.builder()
				.seatId(theaterSeat.getId())
				.seatRow(theaterSeat.getSeatRow())
				.seatNumber(theaterSeat.getNumber())
				.reviewAmount(theaterSeat.getReviewAmount())
				.rating(theaterSeat.getRating())
				.build();
		}

		public static List<Seat> of(Integer floor, String section, List<TheaterSeat> theaterSeats) {
			if (CollectionUtils.isEmpty(theaterSeats)) {
				return Collections.emptyList();
			}
			return theaterSeats.stream()
				.filter(t -> Objects.equals(t.getFloor(), floor) && t.getSection().equals(section))
				.map(Seat::from)
				.collect(Collectors.toList());
		}
	}
}