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
	private Double seatRating; // 좌석 평점
	private Long reviewId;
	private Long userId;
	private Integer floor;
	private String section;
	private String seatRow;
	private Integer seatNumber;
	private Integer rating; // 개인 평점
	private Long likeAmount; // 좋아요 개수
	private String content;
	private String thumbnail;
	private Long commentAmount;

	public static ReviewInfoCond from(Review review) {
		return ReviewInfoCond.builder()
			.seatRating(review.getTheaterSeat().getRating())
			.reviewId(review.getId())
			.userId(review.getMember().getId())
			.floor(review.getTheaterSeat().getFloor())
			.section(review.getTheaterSeat().getSection())
			.seatRow(review.getTheaterSeat().getSeatRow())
			.seatNumber(review.getTheaterSeat().getNumber())
			.rating(review.getRating())
			.likeAmount(review.getLikeAmount())
			.content(review.getContent())
			.thumbnail(review.getThumbnailUrl())
			.commentAmount(review.getCommentAmount())
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
