package com.example.seatchoice.service;

import com.example.seatchoice.dto.cond.ReviewCond;
import com.example.seatchoice.dto.cond.ReviewInfoCond;
import com.example.seatchoice.dto.param.ReviewParam;
import com.example.seatchoice.entity.Image;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.entity.ReviewLike;
import com.example.seatchoice.entity.TheaterSeat;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.ImageRepository;
import com.example.seatchoice.repository.ReviewLikeRepository;
import com.example.seatchoice.repository.ReviewRepository;
import com.example.seatchoice.repository.TheaterSeatRepository;
import com.example.seatchoice.type.ErrorCode;
import java.util.ArrayList;
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
	private final ReviewLikeRepository reviewLikeRepository;
	private final ImageService s3Service;


	// 리뷰 등록
	public ReviewCond createReview(Long theaterId, List<MultipartFile> files, ReviewParam request) {
		// TODO 로그인 된 유저 검증

		List<TheaterSeat> theaterSeats = theaterSeatRepository.findAllByTheaterId(theaterId);
		TheaterSeat theaterSeat = getTheaterSeat(theaterSeats, request);

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

		if (thumbnail != null) {
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

	// 리뷰 상세 조회
	public ReviewInfoCond getReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(
				() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW, HttpStatus.BAD_REQUEST));

		List<Image> imageList = imageRepository.findAllByReviewId(reviewId);
		List<String> images = new ArrayList<>();
		if (!CollectionUtils.isEmpty(imageList)) {
			for (int i = 0; i < imageList.size(); i++) {
				images.add(imageList.get(i).getUrl());
			}
		} else {
			imageList = null;
		}

		return ReviewInfoCond.from(review, getReviewRating(review.getTheaterSeat().getId()),
			getLikeAmount(reviewId), images);
	}

	// 리뷰 등록 시 등록한 좌석 정보로 해당 공연장 좌석 받아오기
	public TheaterSeat getTheaterSeat(List<TheaterSeat> theaterSeats, ReviewParam request) {
		for (TheaterSeat seat : theaterSeats) {
			if (seat.getFloor() == request.getFloor() &&
				seat.getSeatRow().equals(request.getSeatRow()) &&
				seat.getNumber() == request.getSeatNumber()) {
				if (seat.getSection() == null && request.getSection() == null) {
					return seat;
				} else if (seat.getSection() != null && request.getSection() != null) {
					if (seat.getSection().equals(request.getSection())) {
						return seat;
					}
				}
			}
		}

		// 해당 좌석이 없을 때
		throw new CustomException(ErrorCode.NOT_FOUND_SEAT, HttpStatus.BAD_REQUEST);
	}

	// 좌석 평점
	public Double getReviewRating(Long theaterSeatId) {
		List<Review> reviews = reviewRepository.findAllByTheaterSeatId(theaterSeatId);
		Double total = 0.0;
		for (int i = 0; i < reviews.size(); i++) {
			total += reviews.get(i).getRating();
		}
		return Math.round((total / reviews.size()) * 10) / 10.0;
	}

	// 좌석 좋아요 개수
	public Integer getLikeAmount(Long reviewId) {
		List<ReviewLike> reviewLikes = reviewLikeRepository.findAllByReviewId(reviewId);
		if (CollectionUtils.isEmpty(reviewLikes)) return 0;
		return reviewLikes.size();
	}
}
