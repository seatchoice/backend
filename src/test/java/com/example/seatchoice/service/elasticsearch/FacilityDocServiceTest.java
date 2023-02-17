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
		List<FacilityDoc> list = facilityDocService.searchFacility("",
			null, 10, null, null);

		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getName());
			System.out.println(list.get(i).getSido());
			System.out.println(list.get(i).getGugun());
		}

		Assertions.assertNotNull(list);
	}
}
