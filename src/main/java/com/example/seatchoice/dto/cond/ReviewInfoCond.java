package com.example.seatchoice.dto.cond;

import com.example.seatchoice.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewInfoCond {
	private String nickname;
	private LocalDateTime createdAt;
	private Integer floor;
	private String section;
	private String row;
	private Integer seatNumber;
	private Double rating; // 평점
	private Integer likeAmount; // 좋아요 개수
	private String content;
	private List<String> images;


	public static ReviewInfoCond from(Review review, Double rating,
		Integer likeAmount, List<String> images) {
		return ReviewInfoCond.builder()
			.nickname(review.getMember().getNickname())
			.createdAt(review.getMember().getCreatedAt())
			.floor(review.getTheaterSeat().getFloor())
			.section(review.getTheaterSeat().getSection())
			.row(review.getTheaterSeat().getSeatRow())
			.seatNumber(review.getTheaterSeat().getNumber())
			.rating(rating)
			.likeAmount(likeAmount)
			.content(review.getContent())
			.images(images)
			.build();
	}
}
