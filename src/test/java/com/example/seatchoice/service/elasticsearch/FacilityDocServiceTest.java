package com.example.seatchoice.service.elasticsearch;

import com.example.seatchoice.dto.response.FacilityDocResponse;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FacilityDocServiceTest {
	@Autowired
	private FacilityDocService facilityDocService;

	@Test
	void testSearchFacility() {
		List<FacilityDocResponse> list = facilityDocService.searchFacility("서울마포음악창작소(구.뮤지스땅",
			2.333f, null, 10, null, null);

		System.out.println("=============================");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getName());
//			System.out.println(list.get(i).getSido());
//			System.out.println(list.get(i).getGugun());
		}

		List<FacilityDocResponse> list2 = facilityDocService.searchFacility("서울마포음악창작소(구.뮤지스땅스)",
			2.3f, null, 10, null, null);

		System.out.println("=============================");
		for (int i = 0; i < list2.size(); i++) {
			System.out.println(list2.get(i).getName());
		}

		Assertions.assertNotNull(list);
	}
}
