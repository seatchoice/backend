package com.example.seatchoice.config.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.entity.Performance;
import com.example.seatchoice.repository.PerformanceRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class PerformanceItemWriterTest {

	@Mock
	private PerformanceRepository performanceRepository;

	private PerformanceItemWriter performanceItemWriter;

	@Test
	void writeTest() throws Exception {
		// given
		performanceItemWriter = new PerformanceItemWriter(performanceRepository);
		List<Performance> performanceList = List.of(
			Performance.builder().mt20id("AAA1").build(),
			Performance.builder().mt20id("AAA2").build()
		);

		ArgumentCaptor<List<Performance>> captor = ArgumentCaptor.forClass(List.class);

		// when
		performanceItemWriter.write(performanceList);

		// then
		verify(performanceRepository, times(1)).saveAll(captor.capture());
		assertEquals(captor.getValue().size(), 2);
		assertEquals(captor.getValue().get(0).getMt20id(), "AAA1");
		assertEquals(captor.getValue().get(1).getMt20id(), "AAA2");
	}
}