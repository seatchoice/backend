package com.example.seatchoice.controller;

import com.example.seatchoice.dto.cond.ReviewDetailResponse;
import com.example.seatchoice.dto.cond.ReviewInfoResponse;
import com.example.seatchoice.dto.cond.ReviewModifyResponse;
import com.example.seatchoice.dto.cond.ReviewResponse;
import com.example.seatchoice.dto.param.ReviewModifyRequest;
import com.example.seatchoice.dto.param.ReviewRequest;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.service.ReviewService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/theaters/{theaterId}/reviews")
	public ResponseEntity<ReviewResponse> createReview(
		@PathVariable Long theaterId,
		@AuthenticationPrincipal Member member,
		@RequestPart(value = "image", required = false) List<MultipartFile> files,
		@Valid @RequestPart("data") ReviewRequest request) {

		return ResponseEntity.ok(reviewService.createReview(member.getId(), theaterId, files, request));
	}

	// 리뷰 상세보기
	@GetMapping("/reviews/{reviewId}")
	public ResponseEntity<ReviewDetailResponse> getReview(@PathVariable Long reviewId,
		@AuthenticationPrincipal Member member) {
		Long memberId = null;
		if (member != null) {
			memberId = member.getId();
		}

		return ResponseEntity.ok(reviewService.getReview(memberId, reviewId));
	}

	// 리뷰 목록 조회 (무한스크롤)
	@GetMapping("/reviews")
	public ResponseEntity<Slice<ReviewInfoResponse>> getReviews(
		@RequestParam(required = false) Long lastReviewId,
		@RequestParam Long seatId, Pageable pageable) {
		return ResponseEntity.ok(reviewService.getReviews(lastReviewId, seatId, pageable));
	}

	// 리뷰 수정
	@PostMapping("/reviews/{reviewId}")
	public ResponseEntity<ReviewModifyResponse> createReview(
		@PathVariable Long reviewId,
		@RequestPart(value = "image", required = false) List<MultipartFile> files,
		@Valid @RequestPart(value = "data") ReviewModifyRequest request,
		@RequestParam(value = "deleteImages", required = false) List<String> deleteImages) {
		return ResponseEntity.ok(reviewService.updateReview(reviewId, files, request, deleteImages));
	}

	// 리뷰 삭제
	@DeleteMapping("/reviews/{reviewId}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
		reviewService.deleteReview(reviewId);
		return ResponseEntity.ok().build();
	}
}
