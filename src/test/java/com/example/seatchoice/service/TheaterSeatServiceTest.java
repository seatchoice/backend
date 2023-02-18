package com.example.seatchoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.example.seatchoice.dto.cond.TheaterSeatResponse;
import com.example.seatchoice.entity.TheaterSeat;
import com.example.seatchoice.repository.TheaterSeatRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class TheaterSeatServiceTest {

	@Mock
	private TheaterSeatRepository theaterSeatRepository;
	@InjectMocks
	private TheaterSeatService theaterSeatService;

	@Test
	@DisplayName("리뷰가 있는 좌석 조회 성공 - 좌석이 있을 경우")
	void getSeatsWithReviewsSuccess() {
		// given
		List<TheaterSeat> seats = Arrays.asList(
			TheaterSeat.builder()
				.floor(1)
				.section("A")
				.seatRow("1")
				.number(1)
				.reviewAmount(0L)
				.rating(5.0)
				.build(),
			TheaterSeat.builder()
				.floor(2)
				.section("A")
				.seatRow("1")
				.number(1)
				.reviewAmount(2L)
				.rating(3.0)
				.build()
		);

		given(theaterSeatRepository.findAllByTheaterId(anyLong())).willReturn(seats);

		// when
		List<TheaterSeatResponse> seatsWithReviews = theaterSeatService.getSeatsWithReviews(1L);

		// then
		assertEquals(1, seatsWithReviews.size());
		assertEquals(2, seatsWithReviews.get(0).getFloor());
	}

	@Test
	@DisplayName("리뷰가 있는 좌석 조회 성공 - 좌석이 없을 경우")
	void getSeatsWithReviews_noSeats() {
		// given
		given(theaterSeatRepository.findAllByTheaterId(anyLong())).willReturn(null);

		// when
		List<TheaterSeatResponse> seatsWithReviews = theaterSeatService.getSeatsWithReviews(1L);

		// then
		assertEquals(null, seatsWithReviews);
	}
}
