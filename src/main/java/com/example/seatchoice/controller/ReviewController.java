package com.example.seatchoice.controller;

import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.dto.cond.ReviewCond;
import com.example.seatchoice.dto.param.ReviewParam;
import com.example.seatchoice.service.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ReviewController {

	private final ReviewService reviewService;

	// 리뷰등록
	// TODO
	// 로그인 된 user 정보 가져오기
	@PostMapping("/theater-seats/{seatId}/reviews")
	public ApiResponse<ReviewCond> createReview(
		@PathVariable Long seatId,
		@RequestPart(value = "image", required = false) List<MultipartFile> files,
		@RequestPart("data") ReviewParam request) {

		// image file을 선택하지 않았을 때
		if (files.get(0).getSize() == 0) files = null;
		ReviewCond reviewCond = reviewService.createReview(seatId, files, request);
		return new ApiResponse<>(reviewCond);
	}
}
