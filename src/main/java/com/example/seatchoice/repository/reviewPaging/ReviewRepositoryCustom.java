package com.example.seatchoice.repository.reviewPaging;

import com.example.seatchoice.dto.response.ReviewInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {
	Slice<ReviewInfoResponse> searchBySlice(Long lastReviewId, Long seatId, Pageable pageable);
}
