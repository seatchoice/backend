package com.example.seatchoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.dto.request.ReviewModifyRequest;
import com.example.seatchoice.dto.request.ReviewRequest;
import com.example.seatchoice.dto.response.ReviewDetailResponse;
import com.example.seatchoice.dto.response.ReviewInfoResponse;
import com.example.seatchoice.dto.response.ReviewResponse;
import com.example.seatchoice.entity.Image;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.entity.Theater;
import com.example.seatchoice.entity.TheaterSeat;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.ImageRepository;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.repository.ReviewLikeRepository;
import com.example.seatchoice.repository.ReviewRepository;
import com.example.seatchoice.repository.TheaterSeatRepository;
import com.example.seatchoice.type.ErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private ImageRepository imageRepository;
	@Mock
	private TheaterSeatRepository theaterSeatRepository;
	@Mock
	private S3Service s3Service;
	@Mock
	private ReviewLikeRepository reviewLikeRepository;
	@InjectMocks
	private ReviewService reviewService;

	@Test
	@DisplayName("?????? ?????? ?????? - ????????? ????????? ??? ?????? ??????")
	void createReviewSuccessWithoutImageFiles() {
		// given
		Member member = new Member();
		Theater theater = new Theater();
		List<TheaterSeat> theaterSeats = Arrays.asList(
			TheaterSeat.builder()
				.theater(theater)
				.floor(1)
				.section("OP")
				.seatRow("1")
				.number(1)
				.rating(0.0)
				.reviewAmount(0L)
				.build(),
			TheaterSeat.builder()
				.theater(theater)
				.floor(2)
				.section("OP")
				.seatRow("1")
				.number(1)
				.rating(0.0)
				.reviewAmount(0L)
				.build()
		);
		ReviewRequest request = new ReviewRequest(1, "OP", "1", 1, "?????? ???????????????!", 4);

		given(theaterSeatRepository.findAllByTheaterId(anyLong())).willReturn(theaterSeats);
		given(memberRepository.getReferenceById(anyLong())).willReturn(member);
		given(reviewRepository.save(any())).willReturn(
			Review.builder()
				.member(member)
				.theaterSeat(theaterSeats.get(0))
				.rating(4)
				.content("?????? ???????????????!")
				.commentAmount(0L)
				.likeAmount(0L)
				.build()
		);

		ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

		// when
		ReviewResponse response = reviewService.createReview(7L, 562L, null, request);

		// then
		verify(reviewRepository, times(1)).save(captor.capture());
		assertEquals(4, response.getRating());
		assertEquals("?????? ???????????????!", response.getContent());
		assertNull(response.getImages());
	}

	@Test
	@DisplayName("?????? ?????? ?????? - ????????? ????????? ??? ?????? ??????")
	void updateReviewSuccessWithoutImageFiles() {
		// given
		Member member = Member.builder()
			.nickname("????????????")
			.build();
		member.setId(1L);
		TheaterSeat theaterSeat = TheaterSeat.builder()
			.floor(1)
			.section("OP")
			.seatRow("1")
			.number(1)
			.rating(4.0)
			.reviewAmount(1L)
			.build();
		Review review = Review.builder()
			.member(member)
			.theaterSeat(theaterSeat)
			.rating(4)
			.content("?????? ???????????????!")
			.commentAmount(0L)
			.likeAmount(0L)
			.build();
		review.setCreatedAt(LocalDateTime.now());
		List<Image> images = Arrays.asList(
			Image.builder()
				.review(review)
				.url("https1")
				.build(),
			Image.builder()
				.review(review)
				.url("https2")
				.build()
		);
		List<String> deleteImages = List.of("https1");
		ReviewModifyRequest request = new ReviewModifyRequest("?????? ???????????????.", 3);

		given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));

		// image delete
		ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
		doNothing().when(imageRepository).deleteByUrl(valueCapture.capture());
		imageRepository.deleteByUrl(deleteImages.get(0));

		Review updateReview = Review.builder()
			.member(member)
			.theaterSeat(theaterSeat)
			.rating(3)
			.content("?????? ??????")
			.build();
		updateReview.setCreatedAt(LocalDateTime.now());
		given(imageRepository.findAllByReviewId(anyLong())).willReturn(images);
		given(reviewRepository.save(any())).willReturn(updateReview);

		ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

		// when
		reviewService.updateReview(1L, null, request, deleteImages);

		// then
		verify(reviewRepository, times(1)).save(captor.capture());
		assertEquals("?????? ???????????????.", captor.getValue().getContent());
		assertEquals(3, captor.getValue().getRating());
		assertEquals(1, captor.getValue().getTheaterSeat().getReviewAmount());
		assertEquals(3.0, captor.getValue().getTheaterSeat().getRating());
		assertEquals(deleteImages.get(0), valueCapture.getValue());
	}

	@Test
	@DisplayName("?????? ?????? ?????? ??????")
	void getReviewSuccess() {
		// given
		Review review = Review.builder()
			.member(Member.builder().build())
			.theaterSeat(TheaterSeat.builder().build())
			.rating(4)
			.content("?????? ???????????????!")
			.commentAmount(0L)
			.likeAmount(1L)
			.build();
		review.setCreatedAt(LocalDateTime.now());
		List<Image> images = Arrays.asList(
			Image.builder()
				.review(review)
				.url("https1")
				.build(),
			Image.builder()
				.review(review)
				.url("https2")
				.build()
		);

		given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
		given(imageRepository.findAllByReviewId(anyLong())).willReturn(images);
		given(reviewLikeRepository.existsByMemberIdAndReviewId(anyLong(), anyLong()))
			.willReturn(true);

		// when
		ReviewDetailResponse response = reviewService.getReview(1L, 1L);

		// then
		assertEquals(4, response.getRating());
		assertEquals(true, response.getLikeChecked());
		assertEquals(2, response.getImages().size());
	}

	@Test
	@DisplayName("?????? ????????? ?????? ?????? ?????? ?????? ??????")
	void getReviewsSuccess() {
		// given
		List<Review> reviews = Arrays.asList(
			Review.builder()
				.member(Member.builder().build())
				.theaterSeat(TheaterSeat.builder().build())
				.rating(3)
				.content("?????? ???????????????!")
				.commentAmount(0L)
				.likeAmount(1L)
				.build(),
			Review.builder()
				.member(Member.builder().build())
				.theaterSeat(TheaterSeat.builder().build())
				.rating(4)
				.content("?????? Good!")
				.commentAmount(1L)
				.likeAmount(2L)
				.build(),
			Review.builder()
				.member(Member.builder().build())
				.theaterSeat(TheaterSeat.builder().build())
				.rating(2)
				.content("?????? ?????????!")
				.commentAmount(1L)
				.likeAmount(2L)
				.build()
		);

		long id = 3L;
		for (Review r : reviews) {
			r.setId(id--);
		}
		Pageable pageable = new Pageable() {
			@Override
			public int getPageNumber() {
				return 0;
			}

			@Override
			public int getPageSize() {
				return 2;
			}

			@Override
			public long getOffset() {
				return 0;
			}

			@Override
			public Sort getSort() {
				return null;
			}

			@Override
			public Pageable next() {
				return null;
			}

			@Override
			public Pageable previousOrFirst() {
				return null;
			}

			@Override
			public Pageable first() {
				return null;
			}

			@Override
			public Pageable withPage(int pageNumber) {
				return null;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}
		};
		Slice<ReviewInfoResponse> reviewInfos = new SliceImpl<>(
			Objects.requireNonNull(ReviewInfoResponse.of(reviews)), pageable, true);

		given(reviewRepository.searchBySlice(4L, 1L, pageable))
			.willReturn(reviewInfos);

		// when
		Slice<ReviewInfoResponse> response = reviewService.getReviews(4L, 1L, pageable);

		// then
		assertEquals(2, response.getSize());
		assertFalse(response.isLast());
		assertEquals("?????? ???????????????!", response.getContent().get(0).getContent());
	}

	@Test
	@DisplayName("?????? ?????? ??????")
	void deleteReviewSuccess() {
		// given
		Review review = Review.builder()
			.theaterSeat(TheaterSeat.builder()
				.rating(3.3)
				.reviewAmount(3L)
				.build())
			.rating(4)
			.build();

		given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
		ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

		// when
		reviewService.deleteReview(1L);

		// then
		verify(reviewRepository, times(1)).delete(captor.capture());
		assertEquals(2L, captor.getValue().getTheaterSeat().getReviewAmount());
		assertEquals(3.0, captor.getValue().getTheaterSeat().getRating());
	}

	@Test
	@DisplayName("?????? ?????? ?????? - ????????? ???????????? ??????")
	void createReview_notFountSeat() {
		// given
		Theater theater = new Theater();
		theater.setId(1L);
		List<TheaterSeat> theaterSeats = Arrays.asList(
			TheaterSeat.builder()
				.theater(theater)
				.floor(1)
				.section("OP")
				.seatRow("1")
				.number(2)
				.rating(0.0)
				.reviewAmount(0L)
				.build(),
			TheaterSeat.builder()
				.theater(theater)
				.floor(2)
				.section("OP")
				.seatRow("1")
				.number(1)
				.rating(0.0)
				.reviewAmount(0L)
				.build()
		);
		ReviewRequest request = new ReviewRequest(1, "OP", "1", 1, "?????? ???????????????!", 4);

		given(theaterSeatRepository.findAllByTheaterId(anyLong())).willReturn(theaterSeats);

		// when
		CustomException customException = assertThrows(CustomException.class,
			() -> reviewService.createReview(7L, 562L, null, request));

		// then
		assertEquals(customException.getErrorCode(), ErrorCode.NOT_FOUND_SEAT);
	}

	@Test
	@DisplayName("?????? ?????? ?????? - ????????? ?????? ??????")
	void updateReview_notFoundReview() {
		// given
		List<String> deleteImages = Arrays.asList("https1");
		ReviewModifyRequest request = new ReviewModifyRequest("?????? ???????????????.", 3);

		given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class,
			() -> reviewService.updateReview(2L, null, request, deleteImages));

		// then
		assertEquals(customException.getErrorCode(), ErrorCode.NOT_FOUND_REVIEW);
	}

	@Test
	@DisplayName("?????? ?????? ?????? ?????? - ????????? ?????? ??????")
	void getReview_notFoundReview() {
		// given
		given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class,
			() -> reviewService.getReview(1L, 1L));

		// then
		assertEquals(customException.getErrorCode(), ErrorCode.NOT_FOUND_REVIEW);
	}

	@Test
	@DisplayName("?????? ?????? ?????? - ????????? ?????? ??????")
	void deleteReview_notFoundReview() {
		// given
		given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		CustomException customException = assertThrows(CustomException.class,
			() -> reviewService.deleteReview(1L));

		// then
		assertEquals(customException.getErrorCode(), ErrorCode.NOT_FOUND_REVIEW);
	}
}
