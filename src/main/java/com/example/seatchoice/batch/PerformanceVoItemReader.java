package com.example.seatchoice.batch;

import com.example.seatchoice.client.kopis.PerformanceResponse.PerformanceVo;
import com.example.seatchoice.service.KopisService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

@RequiredArgsConstructor
public class PerformanceVoItemReader implements ItemReader<PerformanceVo> {

	private final KopisService kopisService;
	private List<PerformanceVo> performanceVoList;
	private int nextPrfIdx;

	@Override
	public PerformanceVo read()
		throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		if (performanceVoList == null) {
			performanceVoList = kopisService.getPerformanceVoList();
			nextPrfIdx = 0;
		}

		PerformanceVo nextPrf = null;
		if (nextPrfIdx < performanceVoList.size()) {
			nextPrf = performanceVoList.get(nextPrfIdx);
			nextPrfIdx++;
		}

		return nextPrf;
	}
}
