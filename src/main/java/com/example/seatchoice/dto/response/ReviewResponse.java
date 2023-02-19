package com.example.seatchoice.dto.response;

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
public class ReviewResponse {
	private Integer floor;
	private String section;
	private String seatRow;
	private Integer seatNumber;
	private String content;
	private Integer rating;
	List<String> images;

	public static ReviewResponse from(Review review, List<String> images) {
		return ReviewResponse.builder()
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
