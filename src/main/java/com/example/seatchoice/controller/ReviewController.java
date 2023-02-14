package com.example.seatchoice.controller;

import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.dto.cond.ReviewCond;
import com.example.seatchoice.dto.cond.ReviewDetailCond;
import com.example.seatchoice.dto.cond.ReviewInfoCond;
import com.example.seatchoice.dto.cond.ReviewModifyCond;
import com.example.seatchoice.dto.param.ReviewModifyParam;
import com.example.seatchoice.dto.param.ReviewParam;
import com.example.seatchoice.service.ReviewService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
	public ApiResponse<ReviewCond> createReview(
		@PathVariable Long theaterId,
		@AuthenticationPrincipal OAuth2User oAuth2User,
		@RequestPart(value = "image", required = false) List<MultipartFile> files,
		@Valid @RequestPart("data") ReviewParam request) {
		// image file을 선택하지 않았을 때
		if (files.get(0).getSize() == 0) files = null;
		Long memberId = Long.valueOf(oAuth2User.getAttributes().get("id").toString());
		return new ApiResponse<>(reviewService.createReview(memberId, theaterId, files, request));
	}

	// 리뷰 상세보기
	@GetMapping("/reviews/{reviewId}")
	public ApiResponse<ReviewDetailCond> getReview(@PathVariable Long reviewId,
		@AuthenticationPrincipal OAuth2User oAuth2User) {
		Long memberId = null;
		if (oAuth2User != null) {
			memberId = Long.valueOf(oAuth2User.getAttributes().get("id").toString());
		}

		return new ApiResponse<>(reviewService.getReview(memberId, reviewId));
	}

	// 리뷰 목록 조회 (무한스크롤)
	@GetMapping("/reviews")
	public ApiResponse<Slice<ReviewInfoCond>> getReviews(
		@RequestParam(required = false) Long lastReviewId,
		@RequestParam Long seatId, Pageable pageable) {
		return new ApiResponse<>(reviewService.getReviews(lastReviewId, seatId, pageable));
	}

	// 리뷰 수정
	@PostMapping("/reviews/{reviewId}")
	public ApiResponse<ReviewModifyCond> createReview(
		@PathVariable Long reviewId,
		@RequestPart(value = "image", required = false) List<MultipartFile> files,
		@Valid @RequestPart(value = "data", required = false) ReviewModifyParam request,
		@RequestParam(value = "deleteImages", required = false) List<String> deleteImages) {
		// image file을 선택하지 않았을 때
		if (files.get(0).getSize() == 0) files = null;
		return new ApiResponse<>(reviewService.updateReview(reviewId, files, request, deleteImages));
	}

	// 리뷰 삭제
	@DeleteMapping("/reviews/{reviewId}")
	public ApiResponse<Void> deleteReview(@PathVariable Long reviewId) {
		reviewService.deleteReview(reviewId);
		return new ApiResponse<>();
	}
}
