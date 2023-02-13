package com.example.seatchoice.config.batch;

import com.example.seatchoice.client.kopis.PerformanceResponse.Prf;
import com.example.seatchoice.service.KopisService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

@RequiredArgsConstructor
public class PrfItemReader implements ItemReader<Prf> {

	private final KopisService kopisService;
	private List<Prf> prfList;
	private int nextPrfIdx;

	@Override
	public Prf read()
		throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		if (prfList == null) {
			prfList = kopisService.getPrfList();
			nextPrfIdx = 0;
		}

		Prf nextPrf = null;
		if (nextPrfIdx < prfList.size()) {
			nextPrf = prfList.get(nextPrfIdx);
			nextPrfIdx++;
		}

		return nextPrf;
	}
}
