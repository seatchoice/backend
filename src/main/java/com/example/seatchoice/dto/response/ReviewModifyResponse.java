package com.example.seatchoice.dto.response;

import com.example.seatchoice.entity.Image;
import com.example.seatchoice.entity.Review;
import java.time.format.DateTimeFormatter;
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
public class ReviewModifyResponse {

	private Long userId;
	private String nickname;
	private String createdAt;
	private Integer floor;
	private String section;
	private String seatRow;
	private Integer seatNumber;
	private Integer rating; // 평점
	private Long likeAmount; // 좋아요 개수
	private String content;
	private List<String> images;

	public static ReviewModifyResponse from(Review review, List<Image> images) {
		return ReviewModifyResponse.builder()
			.userId(review.getMember().getId())
			.nickname(review.getMember().getNickname())
			.createdAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
			.floor(review.getTheaterSeat().getFloor())
			.section(review.getTheaterSeat().getSection())
			.seatRow(review.getTheaterSeat().getSeatRow())
			.seatNumber(review.getTheaterSeat().getNumber())
			.rating(review.getRating())
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
