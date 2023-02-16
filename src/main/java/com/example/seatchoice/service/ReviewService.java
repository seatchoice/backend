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
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.repository.ReviewLikeRepository;
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
	private final MemberRepository memberRepository;
	private final ImageRepository imageRepository;
	private final TheaterSeatRepository theaterSeatRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ImageService s3Service;
	private final String create = "CREATE";
	private final String update = "UPDATE";
	private final String delete = "DELETE";


	// 리뷰 등록
	public ReviewCond createReview(Long memberId, Long theaterId, List<MultipartFile> files,
		ReviewParam request) {
		// image file을 선택하지 않았을 때
		if (CollectionUtils.isEmpty(files) || files.get(0).getSize() == 0) files = null;

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
				.member(memberRepository.getReferenceById(memberId))
				.theaterSeat(theaterSeat)
				.content(request.getContent())
				.thumbnailUrl(thumbnail)
				.rating(request.getRating())
				.commentAmount(0L)
				.likeAmount(0L)
				.build()
		);

		if (thumbnail != null) {
			saveImages(review, images);
		}

		saveSeatRating(theaterSeat, create, request.getRating(), 0);

		return ReviewCond.from(review, images);
	}

	// 리뷰 상세 조회
	public ReviewDetailCond getReview(Long memberId, Long reviewId) {
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

		// 로그인 하지 않은 user는 좋아요 non-checked
		// 로그인 -> 리뷰 상세 보기 -> 그 리뷰에 대해 이미 좋아요를 눌렀다면 checked
		Boolean likeChecked = false;
		if (reviewLikeRepository.existsByMemberIdAndReviewId(memberId, reviewId)) {
			likeChecked = true;
		}

		return ReviewDetailCond.from(review, images, likeChecked);
	}

	// 리뷰 목록 조회
	public Slice<ReviewInfoCond> getReviews(Long lastReviewId, Long seatId, Pageable pageable) {
		List<Review> reviews = reviewRepository.findAllByTheaterSeatId(seatId);

		// 리뷰가 없을 때, null 반환
		if (CollectionUtils.isEmpty(reviews)) return null;

		// 요청이 처음일 때
		if (lastReviewId == null) lastReviewId = reviews.get(reviews.size() - 1).getId() + 1;
		Slice<ReviewInfoCond> reviewInfoConds = reviewRepository
			.searchBySlice(lastReviewId, seatId, pageable);

		return reviewInfoConds;
	}

	// 리뷰 수정
	public ReviewModifyCond updateReview(Long reviewId, List<MultipartFile> files,
		ReviewModifyParam request, List<String> deleteImages) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(
				() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW, HttpStatus.BAD_REQUEST));
		Integer oldReviewRating = review.getRating();

		// image file을 선택하지 않았을 때
		if (CollectionUtils.isEmpty(files) || files.get(0).getSize() == 0) files = null;

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

		saveSeatRating(review.getTheaterSeat(), update, oldReviewRating, review.getRating());

		return ReviewModifyCond.from(review, savedImages);
	}

	// 리뷰 삭제
	public void deleteReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(
				() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW, HttpStatus.BAD_REQUEST));
		Integer rating = review.getRating();
		TheaterSeat theaterSeat = review.getTheaterSeat();
		List<Image> images = imageRepository.findAllByReviewId(reviewId);

		reviewRepository.deleteCommentById(reviewId);
		reviewRepository.deleteImageById(reviewId);
		reviewRepository.deleteReviewLikeById(reviewId);
		reviewRepository.delete(review);

		// 좌석 평점 수정
		saveSeatRating(theaterSeat, delete, rating, 0);

		// s3에서 이미지 삭제
		if (!CollectionUtils.isEmpty(images)) {
			s3Service.deleteImageFromObject(images);
		}
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

	// 좌석 평점 저장
	public void saveSeatRating(TheaterSeat theaterSeat, String status,
		Integer rating, Integer updateRating) {
		Double total = theaterSeat.getRating() * theaterSeat.getReviewAmount();
		Long reviewAmount = theaterSeat.getReviewAmount();
		if (status.equals("CREATE")) {
			total += rating;
			reviewAmount += 1;
			theaterSeat.setReviewAmount(reviewAmount);
		} else if (status.equals("UPDATE")) {
			total -= rating;
			total += updateRating;
		} else {
			total -= rating;
			reviewAmount = reviewAmount - 1 >= 0 ? reviewAmount - 1 : 0;
			theaterSeat.setReviewAmount(theaterSeat.getReviewAmount() - 1);
		}
		theaterSeat.setRating(Math.round(total / reviewAmount * 10) / 10.0);
		theaterSeatRepository.save(theaterSeat);
	}
}
