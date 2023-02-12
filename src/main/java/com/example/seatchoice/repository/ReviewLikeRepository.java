package com.example.seatchoice.repository;

import com.example.seatchoice.entity.ReviewLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
	List<ReviewLike> findAllByReviewId(Long id);
}
