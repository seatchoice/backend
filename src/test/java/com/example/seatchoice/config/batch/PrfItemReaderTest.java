package com.example.seatchoice.config.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;

import com.example.seatchoice.client.kopis.PerformanceResponse.Prf;
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
class PrfItemReaderTest {

	@Mock
	private KopisService kopisService;

	private PrfItemReader prfItemReader;
	private List<Prf> prfList;

	@Test
	@DisplayName("reader 단위 test")
	public void readTest() throws Exception {
		// given
		prfItemReader = new PrfItemReader(kopisService);
		prfList = Arrays.asList(
			Prf.builder().mt20id("AAA1").build(),
			Prf.builder().mt20id("AAA2").build(),
			Prf.builder().mt20id("AAA3").build()
		);

		given(kopisService.getPrfList()).willReturn(prfList);

		// when
		// then
		Prf firstPrf = prfItemReader.read();
		assertEquals(firstPrf, prfList.get(0));
		Prf secondPrf = prfItemReader.read();
		assertEquals(secondPrf, prfList.get(1));
		Prf thirdPrf = prfItemReader.read();
		assertEquals(thirdPrf, prfList.get(2));
		Prf fourthPrf = prfItemReader.read();
		assertNull(fourthPrf);
	}

}