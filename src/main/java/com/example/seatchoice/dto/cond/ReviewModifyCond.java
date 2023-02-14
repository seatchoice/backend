package com.example.seatchoice.dto.cond;

import com.example.seatchoice.entity.Image;
import com.example.seatchoice.entity.Review;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModifyCond {
	private Long userId;
	private String nickname;
	private LocalDateTime createdAt;
	private Integer floor;
	private String section;
	private String row;
	private Integer seatNumber;
	private Double rating; // 평점
	private Long likeAmount; // 좋아요 개수
	private String content;
	private List<String> images;


	public static ReviewModifyCond from(Review review, Double rating, List<Image> images) {
		return ReviewModifyCond.builder()
			.userId(review.getMember().getId())
			.nickname(review.getMember().getNickname())
			.createdAt(review.getMember().getCreatedAt())
			.floor(review.getTheaterSeat().getFloor())
			.section(review.getTheaterSeat().getSection())
			.row(review.getTheaterSeat().getSeatRow())
			.seatNumber(review.getTheaterSeat().getNumber())
			.rating(rating)
			.likeAmount(review.getLikeAmount())
			.content(review.getContent())
			.images(getImages(images))
			.build();
	}

	public static List<String> getImages(List<Image> images) {
		if (CollectionUtils.isEmpty(images)) {
			return null;
		}

		List<String> list = new ArrayList<>();
		for (Image img : images) {
			list.add(img.getUrl());
		}
		return list;
	}
}
