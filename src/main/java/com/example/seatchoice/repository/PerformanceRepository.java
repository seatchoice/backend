package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Performance;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

	@Modifying
	@Query("DELETE FROM Performance p WHERE p.prfpdto < :nowDate")
	void deleteByEndDate(@Param("nowDate") LocalDate nowDate);

	boolean existsByMt20id(String mt20id);

	@Query("SELECT p FROM Performance p WHERE DATE(p.createdAt) = :now")
	Page<Performance> findByCreatedAt(@Param("now") LocalDate now, Pageable pageable);

}
