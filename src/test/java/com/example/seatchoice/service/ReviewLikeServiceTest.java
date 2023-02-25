package com.example.seatchoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.example.seatchoice.dto.response.AlarmResponse;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.entity.ReviewLike;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.repository.ReviewLikeRepository;
import com.example.seatchoice.repository.ReviewRepository;
import com.example.seatchoice.type.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ReviewLikeServiceTest {

	@Mock
	private ReviewLikeRepository reviewLikeRepository;
	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private AlarmService alarmService;
	@InjectMocks
	private ReviewLikeService reviewLikeService;

	@Test
	@DisplayName("좋아요 생성 성공")
	void createLikeSuccess() {
		// given
		Member member = new Member();
		Review review = Review.builder()
			.member(member)
			.likeAmount(0L)
			.build();

		given(memberRepository.getReferenceById(anyLong())).willReturn(member);
		given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
		given(reviewLikeRepository.existsByMemberIdAndReviewId(anyLong(), anyLong())).willReturn(
			false);
		given(reviewLikeRepository.save(any())).willReturn(ReviewLike.builder()
			.member(member)
			.review(review)
			.build());
		given(reviewRepository.save(any())).willReturn(review);

		// when
		reviewLikeService.createLike(1L, 1L);

		// then
		assertEquals(1L, review.getLikeAmount());
	}

	@Test
	@DisplayName("좋아요 취소 성공")
	void deleteLikeSuccess() {
		// given
		Review review = Review.builder()
			.likeAmount(1L)
			.build();

		given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
		given(reviewLikeRepository.findByMemberIdAndReviewId(anyLong(), anyLong()))
			.willReturn(Optional.of(ReviewLike.builder().build()));
		given(reviewRepository.save(any())).willReturn(review);

		// when
		reviewLikeService.deleteLike(1L, 1L);

		// then
		assertEquals(0L, review.getLikeAmount());
	}

	@Test
	@DisplayName("좋아요 생성 실패 - 해당 리뷰가 없을 경우")
	void createLike_notFoundReview() {
		// given
		given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class,
			() -> reviewLikeService.createLike(1L, 1L));

		// then
		assertEquals(customException.getErrorCode(), ErrorCode.NOT_FOUND_REVIEW);
	}

	@Test
	@DisplayName("좋아요 생성 실패 - 좋아요 더블 클릭 했을 경우")
	void createLike_doubleCheckedLike() {
		// given
		given(reviewRepository.findById(anyLong())).willReturn(
			Optional.of(Review.builder().build()));
		given(reviewLikeRepository.existsByMemberIdAndReviewId(anyLong(), anyLong())).willReturn(
			true);

		// when
		CustomException customException = assertThrows(CustomException.class,
			() -> reviewLikeService.createLike(1L, 1L));

		// then
		assertEquals(customException.getErrorCode(), ErrorCode.DOUBLE_CHECKED_LIKE);
	}

	@Test
	@DisplayName("좋아요 취소 실패 - 해당 리뷰가 없을 경우")
	void deleteLike_notFoundReview() {
		// given
		given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class,
			() -> reviewLikeService.deleteLike(1L, 1L));

		// then
		assertEquals(customException.getErrorCode(), ErrorCode.NOT_FOUND_REVIEW);
	}

	@Test
	@DisplayName("좋아요 취소 실패 - 취소할 좋아요가 없을 경우")
	void deleteLike_notFoundLike() {
		// given
		given(reviewRepository.findById(anyLong())).willReturn(
			Optional.of(Review.builder().build()));
		given(reviewLikeRepository.findByMemberIdAndReviewId(anyLong(), anyLong()))
			.willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class,
			() -> reviewLikeService.deleteLike(1L, 1L));

		// then
		assertEquals(customException.getErrorCode(), ErrorCode.NOT_FOUND_LIKE);
	}
}
