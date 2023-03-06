package com.example.seatchoice.service.elasticsearch;

import com.example.seatchoice.dto.response.PerformanceDocResponse;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PerformanceDocServiceTest {
	@Autowired
	private PerformanceDocService performanceDocService;

	@Test
	void searchPerformanceTest() {
		List<PerformanceDocResponse> list = performanceDocService.searchPerformance("겨",
			2.3f, null, 10, null, null);

		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getName());
		}


		Assertions.assertNotNull(list);
	}
}
