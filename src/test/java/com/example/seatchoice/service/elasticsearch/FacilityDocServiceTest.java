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
		List<FacilityDoc> list = facilityDocService.searchFacility("", null, 10);

		Assertions.assertNotNull(list);
	}
}