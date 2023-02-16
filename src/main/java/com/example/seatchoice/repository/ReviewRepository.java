package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Review;
import com.example.seatchoice.repository.reviewPaging.ReviewRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
	@Modifying
	@Query("delete from Comment c where c.review.id =:id ")
	@Transactional
	void deleteCommentById(Long id);

	@Modifying
	@Query("delete from ReviewLike rl where rl.review.id =:id ")
	@Transactional
	void deleteReviewLikeById(Long id);

	@Modifying
	@Query("delete from Image im where im.review.id =:id ")
	@Transactional
	void deleteImageById(Long id);
}
