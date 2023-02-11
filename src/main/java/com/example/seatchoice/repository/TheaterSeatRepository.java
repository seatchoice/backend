package com.example.seatchoice.repository;

import com.example.seatchoice.entity.TheaterSeat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterSeatRepository extends JpaRepository<TheaterSeat, Long> {
	List<TheaterSeat> findAllByTheaterId(Long id);
}
