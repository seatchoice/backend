package com.example.seatchoice.service.elasticsearch;

import com.example.seatchoice.entity.Performance;
import com.example.seatchoice.entity.document.PerformanceDoc;
import com.example.seatchoice.repository.PerformanceRepository;
import com.example.seatchoice.repository.elasticsearch.PerformanceDocRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceDocService {

	private final PerformanceRepository performanceRepository;
	private final PerformanceDocRepository performanceDocRepository;

	/**
	 * mysql에 저장된 performance es에 저장 (초기 세팅)
	 */
	public void savePerformances() {
		List<Performance> performanceList = performanceRepository.findAll();

		performanceDocRepository.saveAll(performanceList.stream()
			.map(PerformanceDoc::from)
			.collect(Collectors.toList()));
	}

}