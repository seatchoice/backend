package com.example.seatchoice.service;

import com.example.seatchoice.dto.cond.ReviewCond;
import com.example.seatchoice.dto.cond.ReviewDetailCond;
import com.example.seatchoice.dto.cond.ReviewInfoCond;
import com.example.seatchoice.dto.cond.ReviewModifyCond;
import com.example.seatchoice.dto.param.ReviewModifyParam;
import com.example.seatchoice.dto.param.ReviewParam;
import com.example.seatchoice.entity.Image;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.entity.TheaterSeat;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.ImageRepository;
import com.example.seatchoice.repository.ReviewRepository;
import com.example.seatchoice.repository.TheaterSeatRepository;
import com.example.seatchoice.type.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ImageRepository imageRepository;
	private final TheaterSeatRepository theaterSeatRepository;
	private final ImageService s3Service;


	// 리뷰 등록
	public ReviewCond createReview(Long theaterId, List<MultipartFile> files, ReviewParam request) {
		// TODO 로그인 된 유저 검증

		// 별점 표시 안 할 경우 0점으로 처리
		if (request.getRating() == null) request.setRating(0);

		List<TheaterSeat> theaterSeats = theaterSeatRepository.findAllByTheaterId(theaterId);
		TheaterSeat theaterSeat = getTheaterSeat(theaterSeats, request);

		List<String> images = s3Service.uploadImage(files);
		String thumbnail = null;
		if (CollectionUtils.isEmpty(images)) {
			images = null;
		} else {
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
			saveImages(review, images);
		}

		return ReviewCond.from(review, images);
	}

	// 리뷰 상세 조회
	public ReviewDetailCond getReview(Long reviewId) {
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
			images = null;
		}

		List<Review> reviews = reviewRepository.findAllByTheaterSeatId(review.getTheaterSeat().getId());

		return ReviewDetailCond.from(review, getReviewRating(reviews), images);
	}

	// 리뷰 목록 조회
	public Slice<ReviewInfoCond> getReviews(Long lastReviewId, Long seatId, Pageable pageable) {
		List<Review> reviews = reviewRepository.findAllByTheaterSeatId(seatId);

		Double rating = getReviewRating(reviews);

		// 리뷰가 없을 때, null 반환
		if (CollectionUtils.isEmpty(reviews)) return null;

		// 요청이 처음일 때
		if (lastReviewId == null) lastReviewId = reviews.get(reviews.size() - 1).getId();
		Slice<ReviewInfoCond> reviewInfoConds = reviewRepository
			.searchBySlice(lastReviewId, pageable);

		for (ReviewInfoCond reviewInfoCond : reviewInfoConds) {
			reviewInfoCond.setRating(rating);
		}

		return reviewInfoConds;
	}

	// 리뷰 수정
	public ReviewModifyCond updateReview(Long reviewId, List<MultipartFile> files,
		ReviewModifyParam request, List<String> deleteImages) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(
				() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW, HttpStatus.BAD_REQUEST));
		List<Review> reviews = reviewRepository.findAllByTheaterSeatId(review.getTheaterSeat().getId());

		// 별점 표시 안 할 경우 0점으로 처리
		if (request.getRating() == null) request.setRating(0);

		List<String> uploadImages = s3Service.uploadImage(files);
		List<Image> savedImages = new ArrayList<>();
		if (CollectionUtils.isEmpty(deleteImages)) { // 썸네일이 바뀌지 않는 경우
			if (CollectionUtils.isEmpty(uploadImages)) { // 이미지 수정 안 했을 경우
				review = updateReviewByContentAndRating(review, request.getContent(), request.getRating());
				log.info("이미지 수정 안 했을 경우");
			} else { // 이미지 삭제없이 추가만 한 경우
				savedImages = imageRepository.findAllByReviewId(reviewId);
				if (CollectionUtils.isEmpty(savedImages)) { // 기존 이미지가 없던 경우는 썸네일 추가
					review.setThumbnailUrl(uploadImages.get(0));
				}
				review = updateReviewByContentAndRating(review, request.getContent(), request.getRating());
				saveImages(review, uploadImages);
				log.info("이미지 삭제없이 추가만 한 경우");
			}

			savedImages = imageRepository.findAllByReviewId(reviewId);
		} else {
			for (String url : deleteImages) {
				imageRepository.deleteByUrl(url);
			}
			savedImages = imageRepository.findAllByReviewId(reviewId); // 저장된 리뷰에 대한 이미지 불러옴

			if (CollectionUtils.isEmpty(uploadImages)) { // 이미지 삭제만 한 경우
				if (CollectionUtils.isEmpty(savedImages)) { // 리뷰에 대한 모든 이미지가 삭제된 경우
					review.setThumbnailUrl(null);
					log.info("리뷰에 대한 모든 이미지가 삭제된 경우");
				} else {
					review.setThumbnailUrl(savedImages.get(0).getUrl());
					log.info("리뷰에 대한 모든 이미지가 삭제된 경우가 아닌 경우");
				}
				review = updateReviewByContentAndRating(review, request.getContent(), request.getRating());
			} else { // 삭제한 이미지도 있고, 업로드한 이미지도 있는 경우
				review.setThumbnailUrl(getModifiedThumbnailUrl(savedImages, uploadImages));
				review = updateReviewByContentAndRating(review, request.getContent(), request.getRating());
				saveImages(review, uploadImages);
				savedImages = imageRepository.findAllByReviewId(reviewId);
				log.info("삭제한 이미지도 있고, 업로드한 이미지도 있는 경우");
			}

			s3Service.deleteImage(deleteImages);
		}

		return ReviewModifyCond.from(review, getReviewRating(reviews), savedImages);
	}

	// 리뷰 삭제
	public void deleteReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(
				() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW, HttpStatus.BAD_REQUEST));

		reviewRepository.deleteCommentById(reviewId);
		reviewRepository.deleteImageById(reviewId);
		reviewRepository.deleteReviewLikeById(reviewId);
		reviewRepository.delete(review);
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

	public Review updateReviewByContentAndRating(Review review, String content, Integer rating) {
		review.setContent(content);
		review.setRating(rating);
		return reviewRepository.save(review);
	}

	// 이미지 저장
	public void saveImages(Review review, List<String> images) {
		for (String img : images) {
			imageRepository.save(
				Image.builder()
					.review(review)
					.url(img)
					.build());
		}
	}

	public String getModifiedThumbnailUrl(List<Image> savedImages, List<String> uploadImages) {
		if (CollectionUtils.isEmpty(savedImages)) {
			return uploadImages.get(0);
		}
		return savedImages.get(0).getUrl();
	}

	// 좌석 평점
	public Double getReviewRating(List<Review> reviews) {
		Double total = 0.0;
		for (int i = 0; i < reviews.size(); i++) {
			total += reviews.get(i).getRating();
		}
		return Math.round((total / reviews.size()) * 10) / 10.0;
	}
}
