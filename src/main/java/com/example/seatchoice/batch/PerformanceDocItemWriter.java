package com.example.seatchoice.batch;

import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.repository.elasticsearch.PerformanceDocRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class PerformanceDocItemWriter implements ItemWriter<PerformanceDoc> {

	private final PerformanceDocRepository performanceDocRepository;

	@Override
	public void write(List<? extends PerformanceDoc> items) throws Exception {
		performanceDocRepository.saveAll(items);
	}
}
