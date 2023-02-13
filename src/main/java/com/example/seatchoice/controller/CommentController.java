package com.example.seatchoice.controller;

import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.dto.cond.CommentCond;
import com.example.seatchoice.dto.param.CommentParam;
import com.example.seatchoice.service.CommentService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController {

	private final CommentService commentService;

	@GetMapping("/comment")
	public ApiResponse<Void> create(@RequestBody @Valid CommentParam.Create commentParam,
		@AuthenticationPrincipal OAuth2User oAuth2User) {
		Long memberId = Long.valueOf(oAuth2User.getAttributes().get("id").toString());

		commentService.create(memberId, commentParam);

		return new ApiResponse<>();
	}

	@PutMapping("/comment/{commentId}")
	public ApiResponse<Void> modify(@PathVariable Long commentId,
		@RequestBody @Valid CommentParam.Modify commentParam,
		@AuthenticationPrincipal OAuth2User oAuth2User) {

		Long memberId = Long.valueOf(oAuth2User.getAttributes().get("id").toString());

		commentService.modify(memberId, commentId, commentParam);

		return new ApiResponse<>();
	}

	@DeleteMapping("/comment/{commentId}")
	public ApiResponse<Void> delete(@PathVariable Long commentId,
		@AuthenticationPrincipal OAuth2User oAuth2User) {
		Long memberId = Long.valueOf(oAuth2User.getAttributes().get("id").toString());

		commentService.delete(memberId, commentId);

		return new ApiResponse<>();
	}

	@GetMapping("/review/{reviewId}/comments")
	public ApiResponse<List<CommentCond>> list(@PathVariable Long reviewId) {

		return new ApiResponse<>(commentService.list(reviewId));
	}
}
