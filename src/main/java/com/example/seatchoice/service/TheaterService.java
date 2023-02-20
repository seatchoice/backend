package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_FACILITY;

import com.example.seatchoice.dto.response.TheaterResponse;
import com.example.seatchoice.entity.Facility;
import com.example.seatchoice.entity.Theater;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.FacilityRepository;
import com.example.seatchoice.repository.TheaterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TheaterService {

	private final TheaterRepository theaterRepository;
	private final FacilityRepository facilityRepository;


	public TheaterResponse getListOfFacility(Long facilityId) {

		Facility facility = facilityRepository.findById(facilityId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_FACILITY, HttpStatus.NOT_FOUND));

		List<Theater> theaterList = theaterRepository.findAllByFacility(facility);

		return TheaterResponse.of(theaterList, facility);
	}

}
