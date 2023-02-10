package com.example.seatchoice.service.elasticsearch;

import com.example.seatchoice.entity.Facility;
import com.example.seatchoice.entity.document.FacilityDoc;
import com.example.seatchoice.repository.FacilityRepository;
import com.example.seatchoice.repository.elasticsearch.FacilityDocRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityDocService {

	private final FacilityRepository facilityRepository;
	private final FacilityDocRepository facilityDocRepository;

	/**
	 * mysql에 저장된 facility es에 저장 (초기세팅)
	 */
	public void saveFacilities() {
		List<Facility> facilityList = facilityRepository.findAll();

		facilityDocRepository.saveAll(facilityList.stream()
			.map(FacilityDoc::from)
			.collect(Collectors.toList()));
	}

}
