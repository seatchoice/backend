package com.example.seatchoice.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.example.seatchoice.batch.PerformanceVoProcessor;
import com.example.seatchoice.client.kopis.PerformanceResponse.PerformanceVo;
import com.example.seatchoice.entity.Performance;
import com.example.seatchoice.entity.Theater;
import com.example.seatchoice.repository.PerformanceRepository;
import com.example.seatchoice.repository.TheaterRepository;
import com.example.seatchoice.service.KopisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class PerformanceVoProcessorTest {

	@Mock
	private KopisService kopisService;
	@Mock
	private PerformanceRepository performanceRepository;
	@Mock
	private TheaterRepository theaterRepository;

	private PerformanceVoProcessor performanceVoProcessor;

	@Test
	@DisplayName("processer 단위 test - null을 리턴하지 않을 때")
	void processTest_returnNotNull() {
		// given
		performanceVoProcessor = new PerformanceVoProcessor(kopisService, performanceRepository, theaterRepository);
		String facilityOrTheaterName = "예술의 전당 (제로펩시홀)";
		Theater theater = Theater.builder().name("제로펩시홀").build();
		PerformanceVo prf = PerformanceVo.builder()
			.mt20id("AAA1").prfnm("겨울왕국")
			.prfpdfrom("2023.07.18").prfpdto("2023.08.15")
			.poster("http~").genrenm("뮤지컬").openrun("Y")
			.fcltynm("예술의 전당")
			.build();

		given(performanceRepository.existsByMt20id(anyString()))
			.willReturn(false);
		given(kopisService.getFacilityOrTheaterName(anyString()))
			.willReturn(facilityOrTheaterName);
		given(theaterRepository.findByNameAndFacility_Name(anyString(), anyString()))
			.willReturn(theater);

		// when
		Performance performance = performanceVoProcessor.process(prf);

		// then
		assertEquals(performance.getMt20id(), "AAA1");
		assertTrue(performance.isOpenrun());
	}

	@Test
	@DisplayName("processer 단위 test - null을 리턴할 때 - 이미 존재하는 mt20id")
	void processTest_returnNull_existMt20id() {
		// given
		performanceVoProcessor = new PerformanceVoProcessor(kopisService, performanceRepository, theaterRepository);
		PerformanceVo prf = PerformanceVo.builder()
			.mt20id("AAA1").prfnm("겨울왕국")
			.prfpdfrom("2023.07.18").prfpdto("2023.08.15")
			.poster("http~").genrenm("뮤지컬").openrun("Y")
			.fcltynm("예술의 전당")
			.build();

		given(performanceRepository.existsByMt20id(anyString()))
			.willReturn(true);

		// when
		Performance performance = performanceVoProcessor.process(prf);

		// then
		assertNull(performance);

	}

	@Test
	@DisplayName("processer 단위 test - null을 리턴할 때 - open api에서 잘못된 입력값을 넣었을 때")
	void processTest_returnNull_getTheaterNameThrowException() {
		// given
		performanceVoProcessor = new PerformanceVoProcessor(kopisService, performanceRepository, theaterRepository);
		PerformanceVo prf = PerformanceVo.builder()
			.mt20id("AAA1").prfnm("겨울왕국")
			.prfpdfrom("2023.07.18").prfpdto("2023.08.15")
			.poster("http~").genrenm("뮤지컬").openrun("Y")
			.fcltynm("예술의 전당")
			.build();
		String facilityOrTheaterName = "잘못된 공연장 이름";

		given(performanceRepository.existsByMt20id(anyString()))
			.willReturn(false);
		given(kopisService.getFacilityOrTheaterName(anyString()))
			.willReturn(facilityOrTheaterName);

		// when
		Performance performance = performanceVoProcessor.process(prf);

		// then
		assertNull(performance);

	}

	@Test
	@DisplayName("processer 단위 test - null을 리턴할 때 - 공연장 정보가 없을 때")
	void processTest_returnNull_notFoundTheater() {
		// given
		performanceVoProcessor = new PerformanceVoProcessor(kopisService, performanceRepository, theaterRepository);
		String facilityOrTheaterName = "예술의 전당 (제로펩시홀)";
		PerformanceVo prf = PerformanceVo.builder()
			.mt20id("AAA1").prfnm("겨울왕국")
			.prfpdfrom("2023.07.18").prfpdto("2023.08.15")
			.poster("http~").genrenm("뮤지컬").openrun("Y")
			.fcltynm("예술의 전당")
			.build();

		given(performanceRepository.existsByMt20id(anyString()))
			.willReturn(false);
		given(kopisService.getFacilityOrTheaterName(anyString()))
			.willReturn(facilityOrTheaterName);
		given(theaterRepository.findByNameAndFacility_Name(anyString(), anyString()))
			.willReturn(null);

		// when
		Performance performance = performanceVoProcessor.process(prf);

		// then
		assertNull(performance);

	}
}