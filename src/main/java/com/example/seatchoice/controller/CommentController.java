package com.example.seatchoice.controller;

import com.example.seatchoice.dto.request.CommentRequest;
import com.example.seatchoice.dto.response.CommentResponse;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.service.CommentService;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/comments")
	public ResponseEntity<Void> create(@RequestBody @Valid CommentRequest.Create commentRequest,
		@AuthenticationPrincipal Member member) {

		commentService.create(member.getId(), commentRequest);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/comments/{commentId}")
	public ResponseEntity<Void> modify(
		@Parameter(description = "[댓글 id]", example = "12") @PathVariable Long commentId,
		@RequestBody @Valid CommentRequest.Modify commentRequest,
		@AuthenticationPrincipal Member member) {

		commentService.modify(member.getId(), commentId, commentRequest);

		return ResponseEntity.ok().build();
	}


	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> delete(
		@Parameter(description = "[댓글 id]", example = "14") @PathVariable Long commentId,
		@Parameter(hidden = true) @AuthenticationPrincipal Member member) {

		commentService.delete(member.getId(), commentId);

		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/reviews/{reviewId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CommentResponse>> list(
		@Parameter(description = "[리뷰 id]", example = "17")
		@PathVariable Long reviewId) {

		return ResponseEntity.ok(commentService.list(reviewId));
	}
}
