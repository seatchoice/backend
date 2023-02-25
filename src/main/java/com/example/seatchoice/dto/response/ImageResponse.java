package com.example.seatchoice.dto.response;

import com.example.seatchoice.entity.Image;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {

	private Long reviewId;
	private String imageUrl;

	public static ImageResponse from(Image image) {
		return ImageResponse.builder()
			.reviewId(image.getReview().getId())
			.imageUrl(image.getUrl())
			.build();
	}

	public static List<ImageResponse> of(List<Image> images) {
		if (CollectionUtils.isEmpty(images)) {
			return Collections.emptyList();
		}
		return images.stream()
			.map(ImageResponse::from)
			.collect(Collectors.toList());
	}
}
