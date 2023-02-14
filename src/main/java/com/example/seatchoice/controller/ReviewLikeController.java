package com.example.seatchoice.controller;

import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class ReviewLikeController {

	private final ReviewLikeService reviewLikeService;

	// 좋아요 생성
	@PostMapping
	public ApiResponse<Void> createLike(
		@RequestParam Long reviewId, @AuthenticationPrincipal OAuth2User oAuth2User) {
		Long memberId = Long.valueOf(oAuth2User.getAttributes().get("id").toString());
		reviewLikeService.createLike(memberId, reviewId);
		return new ApiResponse<>();
	}

	// 좋아요 취소
	@DeleteMapping
	public ApiResponse<Void> deleteLike(
		@RequestParam Long reviewId, @AuthenticationPrincipal OAuth2User oAuth2User) {
		Long memberId = Long.valueOf(oAuth2User.getAttributes().get("id").toString());
		reviewLikeService.deleteLike(memberId, reviewId);
		return new ApiResponse<>();
	}
}
