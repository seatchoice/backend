package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Review;
import com.example.seatchoice.repository.reviewPaging.ReviewRepositoryCustom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
	List<Review> findAllByTheaterSeatId(Long id);

	@Modifying
	@Query("delete from Comment c where c.review.id =:id ")
	void deleteCommentById(Long id);

	@Modifying
	@Query("delete from ReviewLike rl where rl.review.id =:id ")
	void deleteReviewLikeById(Long id);

	@Modifying
	@Query("delete from Image im where im.review.id =:id ")
	void deleteImageById(Long id);
}
