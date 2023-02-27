package com.example.seatchoice.service;

import com.example.seatchoice.dto.response.TheaterSeatResponse;
import com.example.seatchoice.entity.TheaterSeat;
import com.example.seatchoice.repository.TheaterSeatRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheaterSeatService {

	private final TheaterSeatRepository theaterSeatRepository;

	public List<TheaterSeatResponse> getSeats(Long theaterId) {
		List<Integer> floorList = theaterSeatRepository.findDistinctFloorByTheaterId(theaterId);
		if (CollectionUtils.isEmpty(floorList)) {
			return Collections.emptyList();
		}

		List<String> sectionList = theaterSeatRepository.findDistinctSectionByTheaterId(theaterId);
		List<TheaterSeatResponse.Section> sections;
		List<TheaterSeatResponse> theaterSeatResponses = new ArrayList<>();
		List<TheaterSeat> theaterSeats = theaterSeatRepository.findAllByTheaterId(theaterId);

		for (Integer floor : floorList) {
			sections = new ArrayList<>();
			for (String section : sectionList) {
				sections.add(TheaterSeatResponse.Section.from(section,
					TheaterSeatResponse.Seat.of(floor, section, theaterSeats)));
			}
			theaterSeatResponses.add(TheaterSeatResponse.from(floor, sections));
		}

		return theaterSeatResponses;
	}
}
