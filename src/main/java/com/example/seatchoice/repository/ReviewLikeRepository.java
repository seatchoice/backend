package com.example.seatchoice.repository;

import com.example.seatchoice.entity.ReviewLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
	Optional<ReviewLike> findByMemberIdAndReviewId(Long memberId, Long reviewId);
	boolean existsByMemberIdAndReviewId(Long memberId, Long reviewId);
}
