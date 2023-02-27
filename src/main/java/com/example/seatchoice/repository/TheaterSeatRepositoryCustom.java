package com.example.seatchoice.repository;

import java.util.List;

public interface TheaterSeatRepositoryCustom {

	List<Integer> findDistinctFloorByTheaterId(Long theaterId);

	List<String> findDistinctSectionByTheaterId(Long theaterId);
}
