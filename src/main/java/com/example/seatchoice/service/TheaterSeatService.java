package com.example.seatchoice.service;

import com.example.seatchoice.dto.cond.TheaterSeatCond;
import com.example.seatchoice.entity.TheaterSeat;
import com.example.seatchoice.repository.TheaterSeatRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class TheaterSeatService {

	private final TheaterSeatRepository theaterSeatRepository;
	public List<TheaterSeatCond> getSeatsWithReviews(Long theaterId) {
		List<TheaterSeat> seats = theaterSeatRepository.findAllByTheaterId(theaterId);

		List<TheaterSeatCond> theaterSeatConds = new ArrayList<>();
		if (!CollectionUtils.isEmpty(seats)) {
			for (TheaterSeat seat : seats) {
				if (seat.getReviewAmount() > 0) {
					theaterSeatConds.add(TheaterSeatCond.from(seat));
				}
			}
		} else {
			theaterSeatConds = null;
		}

		return theaterSeatConds;
	}
}
