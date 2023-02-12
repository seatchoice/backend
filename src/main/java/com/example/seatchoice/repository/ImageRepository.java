package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findAllByReviewId(Long id);
}
