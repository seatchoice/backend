package com.example.seatchoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.example.seatchoice.dto.response.TheaterResponse;
import com.example.seatchoice.entity.Facility;
import com.example.seatchoice.entity.Theater;
import com.example.seatchoice.repository.FacilityRepository;
import com.example.seatchoice.repository.TheaterRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class TheaterServiceTest {

	@Mock
	private TheaterRepository theaterRepository;
	@Mock
	private FacilityRepository facilityRepository;
	@InjectMocks
	private TheaterService theaterService;

	@Test
	void getListOfFacility() {
		// given
		Facility facility = Facility.builder()
			.name("서울아트센터").build();
		facility.setId(1L);
		Theater theater1 = Theater.builder().name("백엔홀").seatCnt(230).build();
		theater1.setId(11L);
		Theater theater2 = Theater.builder().name("프엔홀").seatCnt(500).build();
		theater2.setId(12L);
		List<Theater> theaterList = List.of(theater1, theater2);

		given(facilityRepository.findById(anyLong()))
			.willReturn(Optional.of(facility));
		given(theaterRepository.findAllByFacility(any()))
			.willReturn(theaterList);

		// when
		TheaterResponse response = theaterService.getListOfFacility(1L);

		// then
		assertEquals(response.getFacilityId(), 1L);
		assertEquals(response.getFacilityName(), "서울아트센터");
		assertEquals(response.getTheaterList().size(), 2);
		assertEquals(response.getTheaterList().get(0).getId(), 11L);
		assertEquals(response.getTheaterList().get(1).getId(), 12L);
		assertEquals(response.getTheaterList().get(0).getName(), "백엔홀");
		assertEquals(response.getTheaterList().get(1).getName(), "프엔홀");
		assertEquals(response.getTheaterList().get(0).getSeatCnt(), 230);
		assertEquals(response.getTheaterList().get(1).getSeatCnt(), 500);

	}
}