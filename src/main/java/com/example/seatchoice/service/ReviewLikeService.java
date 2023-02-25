package com.example.seatchoice.service;

import com.example.seatchoice.entity.Member;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.entity.ReviewLike;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.repository.ReviewLikeRepository;
import com.example.seatchoice.repository.ReviewRepository;
import com.example.seatchoice.type.AlarmType;
import com.example.seatchoice.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewLikeService {

	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewRepository reviewRepository;
	private final MemberRepository memberRepository;
	private final AlarmService alarmService;

	public void createLike(Long memberId, Long reviewId) {
		Member member = memberRepository.getReferenceById(memberId);
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(
				() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW, HttpStatus.BAD_REQUEST));

		if (reviewLikeRepository.existsByMemberIdAndReviewId(memberId, reviewId)) {
			throw new CustomException(ErrorCode.DOUBLE_CHECKED_LIKE, HttpStatus.BAD_REQUEST);
		}

		reviewLikeRepository.save(ReviewLike.builder()
			.member(member)
			.review(review)
			.build());

		review.setLikeAmount(review.getLikeAmount() + 1);
		reviewRepository.save(review);

		// 알람 생성
		alarmService.createAlarm(memberId, AlarmType.LIKE, "", reviewId, memberId);
	}

	public void deleteLike(Long memberId, Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(
				() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW, HttpStatus.BAD_REQUEST));

		ReviewLike reviewLike = reviewLikeRepository.findByMemberIdAndReviewId(memberId, reviewId)
			.orElseThrow(
				() -> new CustomException(ErrorCode.NOT_FOUND_LIKE, HttpStatus.BAD_REQUEST));

		reviewLikeRepository.delete(reviewLike);

		review.setLikeAmount(review.getLikeAmount() - 1 >= 0 ? review.getLikeAmount() - 1 : 0);
		reviewRepository.save(review);
	}
}
