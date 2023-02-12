package com.example.seatchoice.dto.cond;

import com.example.seatchoice.entity.Review;
import java.util.List;
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
public class ReviewInfoCond {
	private Long reviewId;
	private Long userId;
	private Integer floor;
	private String section;
	private String row;
	private Integer seatNumber;
	private Double rating; // 평점
	private Integer likeAmount; // 좋아요 개수
	private String content;
	private String thumbnail;
	private Integer commentAmount;

	public static ReviewInfoCond from(Review review) {
		return ReviewInfoCond.builder()
			.reviewId(review.getId())
			.userId(review.getMember().getId())
			.floor(review.getTheaterSeat().getFloor())
			.section(review.getTheaterSeat().getSection())
			.row(review.getTheaterSeat().getSeatRow())
			.seatNumber(review.getTheaterSeat().getNumber())
			.rating(null)
			.likeAmount(null)
			.content(review.getContent())
			.thumbnail(review.getThumbnailUrl())
			.commentAmount(null)
			.build();
	}

	public static List<ReviewInfoCond> of(List<Review> reviews) {
		if (CollectionUtils.isEmpty(reviews)) {
			return null;
		}
		return reviews.stream()
			.map(ReviewInfoCond::from)
			.collect(Collectors.toList());
	}
}
