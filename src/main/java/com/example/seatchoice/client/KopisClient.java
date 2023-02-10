package com.example.seatchoice.client;

import com.example.seatchoice.client.kopis.PerformanceDetailResponse;
import com.example.seatchoice.client.kopis.PerformanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kopis", url = "${kopis.api.url}")

public interface KopisClient {

	// 공연 목록
	@GetMapping("/pblprfr")
	PerformanceResponse getPrfList(
		@RequestParam("service") String service,
		@RequestParam("stdate")  String stdate,
		@RequestParam("eddate") String eddate,
		@RequestParam("cpage") Integer cpage,
		@RequestParam("rows") Integer rows,
		@RequestParam("prfstate") String prfstate);

	// 공연 상세 목록
	@GetMapping("/pblprfr/{mt20id}")
	PerformanceDetailResponse getPrfDetail(
		@PathVariable("mt20id") String mt20id,
		@RequestParam("service") String service);

}
