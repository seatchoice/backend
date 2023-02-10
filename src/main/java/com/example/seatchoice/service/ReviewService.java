package com.example.seatchoice.service;

import com.example.seatchoice.dto.cond.ReviewCond;
import com.example.seatchoice.dto.param.ReviewParam;
import com.example.seatchoice.entity.Image;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.entity.TheaterSeat;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.ImageRepository;
import com.example.seatchoice.repository.ReviewRepository;
import com.example.seatchoice.repository.TheaterSeatRepository;
import com.example.seatchoice.type.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ImageRepository imageRepository;
	private final TheaterSeatRepository theaterSeatRepository;
	private final ImageService s3Service;


	public ReviewCond createReview(Long seatId, List<MultipartFile> files, ReviewParam request) {
		// TODO 로그인 된 유저 검증

		TheaterSeat theaterSeat = theaterSeatRepository.findById(seatId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SEAT, HttpStatus.BAD_REQUEST));

		List<String> images = s3Service.uploadImage(files);

		String thumbnail = null;
		if (!CollectionUtils.isEmpty(images)) {
			thumbnail = images.get(0);
		}

		Review review = reviewRepository.save(
			Review.builder()
				.theaterSeat(theaterSeat)
				.content(request.getContent())
				.thumbnailUrl(thumbnail)
				.rating(request.getRating())
				.build()
		);

		if (!CollectionUtils.isEmpty(images)) {
			for (String img : images) {
				imageRepository.save(
					Image.builder()
						.review(review)
						.url(img)
						.build());
			}
		}

		return ReviewCond.from(review, images);
	}
}
