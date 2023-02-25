package com.example.seatchoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.dto.response.ImageResponse;
import com.example.seatchoice.entity.Image;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.entity.TheaterSeat;
import com.example.seatchoice.repository.ImageRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

	@Mock
	private ImageRepository imageRepository;
	@InjectMocks
	private ImageService imageService;

	@Test
	@DisplayName("이미지 저장 성공")
	void saveImagesSuccess() {
		// given
		TheaterSeat theaterSeat = new TheaterSeat();
		theaterSeat.setId(1L);
		Review review = Review.builder().
			theaterSeat(theaterSeat)
			.build();
		Image image = Image.builder().build();
		List<String> images = Arrays.asList("http1");

		given(imageRepository.save(any())).willReturn(image);

		ArgumentCaptor<Image> captor = ArgumentCaptor.forClass(Image.class);

		// when
		imageService.saveImages(review, images);

		// then
		verify(imageRepository, times(1)).save(captor.capture());
		assertEquals("http1", captor.getValue().getUrl());
	}

	@Test
	@DisplayName("이미지 조회 성공")
	void getImagesSuccess() {
		// given
		Review review = new Review();
		review.setId(1L);
		List<Image> images = Arrays.asList(
			Image.builder()
				.review(review)
				.url("http1")
				.build(),
			Image.builder()
				.review(review)
				.url("http2")
				.build()
		);

		given(imageRepository.findAllBySeatId(anyLong())).willReturn(images);

		// when
		List<ImageResponse> imageResponses = imageService.getImages(1L);

		// then
		assertEquals(2, imageResponses.size());
		assertEquals(1L, imageResponses.get(0).getReviewId());
		assertEquals("http1", imageResponses.get(0).getImageUrl());
	}

	@Test
	@DisplayName("이미지 조회 성공 - 빈 list")
	void getImagesSuccess_EmptyList() {
		// given
		Review review = new Review();
		review.setId(1L);
		List<Image> images = Collections.emptyList();

		given(imageRepository.findAllBySeatId(anyLong())).willReturn(images);

		// when
		List<ImageResponse> imageResponses = imageService.getImages(1L);

		// then
		assertEquals(0, imageResponses.size());
		assertEquals(Collections.emptyList(), imageResponses);
	}
}
