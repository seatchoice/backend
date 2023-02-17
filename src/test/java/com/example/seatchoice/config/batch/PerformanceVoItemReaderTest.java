package com.example.seatchoice.config.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;

import com.example.seatchoice.client.kopis.PerformanceResponse.PerformanceVo;
import com.example.seatchoice.service.KopisService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class PerformanceVoItemReaderTest {

	@Mock
	private KopisService kopisService;

	private PerformanceVoItemReader performanceVoItemReader;
	private List<PerformanceVo> prfList;

	@Test
	@DisplayName("reader 단위 test")
	public void readTest() throws Exception {
		// given
		performanceVoItemReader = new PerformanceVoItemReader(kopisService);
		prfList = Arrays.asList(
			PerformanceVo.builder().mt20id("AAA1").build(),
			PerformanceVo.builder().mt20id("AAA2").build(),
			PerformanceVo.builder().mt20id("AAA3").build()
		);

		given(kopisService.getPerformanceVoList()).willReturn(prfList);

		// when
		// then
		PerformanceVo firstPrf = performanceVoItemReader.read();
		assertEquals(firstPrf, prfList.get(0));
		PerformanceVo secondPrf = performanceVoItemReader.read();
		assertEquals(secondPrf, prfList.get(1));
		PerformanceVo thirdPrf = performanceVoItemReader.read();
		assertEquals(thirdPrf, prfList.get(2));
		PerformanceVo fourthPrf = performanceVoItemReader.read();
		assertNull(fourthPrf);
	}

}