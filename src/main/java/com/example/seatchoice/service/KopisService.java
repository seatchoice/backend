package com.example.seatchoice.service;

import com.example.seatchoice.client.KopisClient;
import com.example.seatchoice.client.kopis.PerformanceResponse.PerformanceVo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KopisService {

	@Value("${kopis.api.key}")
	private String kopisKey;
	private final KopisClient kopisClient;

	public List<PerformanceVo> getPerformanceVoList() {
		LocalDate startDt = LocalDate.now().plusMonths(5);
		String startDtStr = startDt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String edDtStr = startDt.plusMonths(1).minusDays(1)
			.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		return kopisClient.getPerformanceVoList(kopisKey,
			startDtStr, edDtStr, 1, 500, "01").getPerformanceVoList();
	}

	public String getFacilityOrTheaterName(String mt20id) {
		return kopisClient.getPerformanceDetailVoList(mt20id, kopisKey)
			.getPerformanceDetailVo().getFcltynm();

	}

}
