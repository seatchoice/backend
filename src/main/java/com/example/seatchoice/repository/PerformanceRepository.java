package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Performance;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

	@Modifying
	@Query("DELETE FROM Performance p WHERE p.prfpdto > :nowDate")
	void deleteByEndDate(@Param("nowDate") LocalDate nowDate);
}
