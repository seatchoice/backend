package com.example.seatchoice.service;

import com.example.seatchoice.dto.cond.TheaterSeatResponse;
import com.example.seatchoice.entity.TheaterSeat;
import com.example.seatchoice.repository.TheaterSeatRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class TheaterSeatService {

	private final TheaterSeatRepository theaterSeatRepository;
	public List<TheaterSeatResponse> getSeatsWithReviews(Long theaterId) {
		List<TheaterSeat> seats = theaterSeatRepository.findAllByTheaterId(theaterId);

		if (!CollectionUtils.isEmpty(seats)) {
			return seats.stream()
				.filter(t -> t.getReviewAmount() > 0)
				.map(TheaterSeatResponse::from)
				.collect(Collectors.toList());
		} else {
			return null;
		}
	}
}
