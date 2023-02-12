package com.example.seatchoice.repository.reviewPaging;

import com.example.seatchoice.dto.cond.ReviewInfoCond;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {
	Slice<ReviewInfoCond> searchBySlice(Long lastReviewId, Pageable pageable);
}
