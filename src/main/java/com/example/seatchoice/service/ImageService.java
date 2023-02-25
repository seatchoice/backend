package com.example.seatchoice.service;

import com.example.seatchoice.dto.response.ImageResponse;
import com.example.seatchoice.entity.Image;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.repository.ImageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

	private final ImageRepository imageRepository;

	public void saveImages(Review review, List<String> images) {
		for (String img : images) {
			imageRepository.save(
				Image.builder()
					.review(review)
					.seatId(review.getTheaterSeat().getId())
					.url(img)
					.build());
		}
	}

	public List<ImageResponse> getImages(Long seatId) {
		List<Image> images = imageRepository.findAllBySeatId(seatId);
		return ImageResponse.of(images);
	}
}
