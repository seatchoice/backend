package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findAllByTheaterSeatId(Long id);
}
