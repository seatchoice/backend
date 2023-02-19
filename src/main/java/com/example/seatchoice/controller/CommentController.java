package com.example.seatchoice.controller;

import com.example.seatchoice.dto.param.CommentRequest;
import com.example.seatchoice.dto.cond.CommentResponse;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.service.CommentService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/comments")
	public ResponseEntity<Void> create(@RequestBody @Valid CommentRequest.Create commentRequest,
		@AuthenticationPrincipal Member member) {

		commentService.create(member.getId(), commentRequest);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/comments/{commentId}")
	public ResponseEntity<Void> modify(@PathVariable Long commentId,
		@RequestBody @Valid CommentRequest.Modify commentRequest,
		@AuthenticationPrincipal Member member) {

		commentService.modify(member.getId(), commentId, commentRequest);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> delete(@PathVariable Long commentId,
		@AuthenticationPrincipal Member member) {

		commentService.delete(member.getId(), commentId);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/reviews/{reviewId}/comments")
	public ResponseEntity<List<CommentResponse>> list(@PathVariable Long reviewId) {

		return ResponseEntity.ok(commentService.list(reviewId));
	}
}
