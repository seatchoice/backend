package com.example.seatchoice.dto.cond;

import com.example.seatchoice.entity.Review;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCond {
	private Integer floor;
	private String section;
	private String seatRow;
	private Integer seatNumber;
	private String content;
	private Integer rating;
	List<String> images;

	public static ReviewCond from(Review review, List<String> images) {
		return ReviewCond.builder()
			.floor(review.getTheaterSeat().getFloor())
			.section(review.getTheaterSeat().getSection())
			.seatRow(review.getTheaterSeat().getSeatRow())
			.seatNumber(review.getTheaterSeat().getNumber())
			.content(review.getContent())
			.rating(review.getRating())
			.images(images)
			.build();
	}
}
