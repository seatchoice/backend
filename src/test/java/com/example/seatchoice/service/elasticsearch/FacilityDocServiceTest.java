package com.example.seatchoice.service.elasticsearch;

import com.example.seatchoice.entity.document.FacilityDoc;
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
		List<FacilityDoc> list = facilityDocService.searchFacility("서울마포음악창작소(구.뮤지스땅",
			null, 10, null, null);

		System.out.println("=============================");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getName());
//			System.out.println(list.get(i).getSido());
//			System.out.println(list.get(i).getGugun());
		}

		List<FacilityDoc> list2 = facilityDocService.searchFacility("서울마포음악창작소(구.뮤지스땅스)",
			null, 10, null, null);

		System.out.println("=============================");
		for (int i = 0; i < list2.size(); i++) {
			System.out.println(list2.get(i).getName());
		}

		List<FacilityDoc> list3 = facilityDocService.searchFacility("예술의전당",
			null, 10, null, null);

		System.out.println("=============================");
		for (int i = 0; i < list3.size(); i++) {
			System.out.println(list3.get(i).getName());
		}

		List<FacilityDoc> list4 = facilityDocService.searchFacility("봄 (봄소극장)",
			null, 10, null, null);

		System.out.println("=============================");
		for (int i = 0; i < list4.size(); i++) {
			System.out.println(list4.get(i).getName());
		}

		Assertions.assertNotNull(list);
	}
}
