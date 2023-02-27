package com.example.seatchoice.repository;

import static com.example.seatchoice.entity.QTheaterSeat.theaterSeat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@RequiredArgsConstructor
public class TheaterSeatRepositoryImpl implements TheaterSeatRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Integer> findDistinctFloorByTheaterId(Long theaterId) {
		List<Integer> floors = queryFactory
			.select(theaterSeat.floor).distinct()
			.from(theaterSeat)
			.where(theaterSeat.theater.id.eq(theaterId))
			.fetch();

		if (CollectionUtils.isEmpty(floors)) {
			return Collections.emptyList();
		}
		return floors;
	}

	@Override
	public List<String> findDistinctSectionByTheaterId(Long theaterId) {
		List<String> sections = queryFactory
			.select(theaterSeat.section).distinct()
			.from(theaterSeat)
			.where(theaterSeat.theater.id.eq(theaterId))
			.fetch();

		if (CollectionUtils.isEmpty(sections)) {
			return Collections.emptyList();
		}
		return sections;
	}
}
