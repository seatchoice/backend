package com.example.seatchoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.dto.cond.ReviewCond;
import com.example.seatchoice.dto.param.ReviewParam;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.entity.Review;
import com.example.seatchoice.entity.Theater;
import com.example.seatchoice.entity.TheaterSeat;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.ImageRepository;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.repository.ReviewLikeRepository;
import com.example.seatchoice.repository.ReviewRepository;
import com.example.seatchoice.repository.TheaterRepository;
import com.example.seatchoice.repository.TheaterSeatRepository;
import com.example.seatchoice.type.ErrorCode;
import java.util.Arrays;
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
	private TheaterRepository theaterRepository;
	@Mock
	private ReviewLikeRepository reviewLikeRepository;
	@Mock
	private ImageService imageService;

	@InjectMocks
	private ReviewService reviewService;

	@Test
	@DisplayName("리뷰 등록 성공 - 이미지 업로드 안 했을 경우")
	void createSuccessWithoutImageFiles() {
	    // given
		Member member = new Member();
		Theater theater = new Theater();
		theater.setId(1L);
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
		ReviewParam request = new ReviewParam(1, "OP", "1", 1, "좌석 괜찮습니다!", 4);

		given(theaterSeatRepository.findAllByTheaterId(anyLong())).willReturn(theaterSeats);
		given(memberRepository.getReferenceById(anyLong())).willReturn(member);
		given(reviewRepository.save(any())).willReturn(
			Review.builder()
				.member(member)
				.theaterSeat(theaterSeats.get(0))
				.rating(4)
				.content("좌석 괜찮습니다!")
				.commentAmount(0L)
				.likeAmount(0L)
				.build()
		);

		ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

	    // when
		ReviewCond reviewCond = reviewService.createReview(7L, 562L, null, request);

		// then
		verify(reviewRepository, times(1)).save(captor.capture());
		assertEquals(4, reviewCond.getRating());
		assertEquals("좌석 괜찮습니다!", reviewCond.getContent());
		assertEquals(null, reviewCond.getImages());
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 리뷰 존재하지 않는 경우")
	void createReview_notFountSeat() {
	    // given
		Member member = new Member();
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
		ReviewParam request = new ReviewParam(1, "OP", "1", 1, "좌석 괜찮습니다!", 4);

		given(theaterSeatRepository.findAllByTheaterId(anyLong())).willReturn(theaterSeats);

		// when
		CustomException customException = assertThrows(CustomException.class,
			() -> reviewService.createReview(7L, 562L, null, request));

		// then
		assertEquals(customException.getErrorCode(), ErrorCode.NOT_FOUND_SEAT);
	}
}
