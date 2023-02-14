package com.example.seatchoice.config.batch;

import com.example.seatchoice.client.kopis.PerformanceResponse.Prf;
import com.example.seatchoice.entity.Performance;
import com.example.seatchoice.entity.Theater;
import com.example.seatchoice.repository.PerformanceRepository;
import com.example.seatchoice.repository.TheaterRepository;
import com.example.seatchoice.service.KopisService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class PrfProcessor implements ItemProcessor<Prf, Performance> {
	private final DataShareBean<Performance> dataShareBean;

	private final KopisService kopisService;
	private final PerformanceRepository performanceRepository;
	private final TheaterRepository theaterRepository;

	@Override
	public Performance process(Prf item) {
		boolean isExistMt20id = performanceRepository.existsByMt20id(item.getMt20id());
		if (isExistMt20id) {
			return null;
		}

		String theaterName = "";
		try {
			theaterName = getTheaterName(item.getMt20id());
		} catch (Exception e) {
			return null;
		}

		if (Objects.equals(theaterName, "")) {
			return null;
		}

		Theater theater = theaterRepository
			.findByNameAndFacility_Name(theaterName, item.getFcltynm());
		if (theater == null) {
			return null;
		}

		Performance performance = Performance.of(
			item, theater, item.getPrfpdfromDate(), item.getPrfpdtoDate());

		dataShareBean.addData("PERFORMANCE", performance);

		return performance;
	}

	private String getTheaterName(String mt20id) throws Exception {
		String facilityOrTheaterName = kopisService.getFacilityOrTheaterName(mt20id);

		String[] strings = facilityOrTheaterName.split("\\)", -1);

		String result = "";
		if (strings.length > 3) {
			result = strings[1].trim().substring(1) + ")";
		} else if (strings.length > 2) {
			if (Objects.equals(strings[1], "") && Objects.equals(strings[2], "")) {
				result = strings[0].split("\\(", 2)[1] + ")";
			} else {
				result = strings[1].substring(2);
			}
		} else if (strings.length > 1) {
			result = facilityOrTheaterName.split("\\(", -1)[1].substring(0, facilityOrTheaterName.split("\\(")[1].length() - 1);
		}

		return result;
	}
}