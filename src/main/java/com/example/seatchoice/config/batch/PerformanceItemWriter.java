package com.example.seatchoice.config.batch;

import com.example.seatchoice.entity.Performance;
import com.example.seatchoice.repository.PerformanceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class PerformanceItemWriter implements ItemWriter<Performance> {

	private final PerformanceRepository performanceRepository;

	@Override
	public void write(List<? extends Performance> items) throws Exception {
		performanceRepository.saveAll(items);
	}

}
