package com.example.seatchoice.dto.cond;

import com.example.seatchoice.entity.TheaterSeat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheaterSeatCond {
	private Long seatId;
	private Integer floor;
	private String section;
	private String seatRow;
	private Integer seatNumber;
	private Long reviewAmount;
	private Double rating; // 평점

	public static TheaterSeatCond from(TheaterSeat seat) {
		return TheaterSeatCond.builder()
			.seatId(seat.getId())
			.floor(seat.getFloor())
			.section(seat.getSection())
			.seatRow(seat.getSeatRow())
			.seatNumber(seat.getNumber())
			.reviewAmount(seat.getReviewAmount())
			.rating(seat.getRating())
			.build();
	}
}
