package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query(
		value =
			"SELECT"
				+ " c.id as id,"
				+ " c.content as content, "
				+ " c.updatedAt as updatedAt, "
				+ " m.nickname as nickname, "
				+ " m.id as userId "
				+ " FROM Comment c"
				+ " JOIN Member m"
				+ " ON c.member.id = m.id"
				+ " WHERE c.review.id = :reviewId "
	)
	List<Comment> findAllByReview(Long reviewId);

}
