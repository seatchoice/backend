package com.example.seatchoice.controller;

import com.example.seatchoice.entity.Member;
import com.example.seatchoice.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
	public ResponseEntity<Void> createLike(
		@RequestParam Long reviewId, @AuthenticationPrincipal Member member) {
		reviewLikeService.createLike(member.getId(), reviewId);
		return ResponseEntity.ok().build();
	}

	// 좋아요 취소
	@DeleteMapping
	public ResponseEntity<Void> deleteLike(
		@RequestParam Long reviewId, @AuthenticationPrincipal Member member) {
		reviewLikeService.deleteLike(member.getId(), reviewId);
		return ResponseEntity.ok().build();
	}
}
