package com.example.seatchoice.controller;

import com.example.seatchoice.dto.common.ErrorResponse;
import com.example.seatchoice.dto.request.CommentRequest;
import com.example.seatchoice.dto.response.CommentResponse;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "comment", description = "댓글 API")
public class CommentController {

	private final CommentService commentService;

	@Tag(name = "comment")
	@Operation(
		summary =  "댓글을 작성합니다.",
		description = "선택한 리뷰에 댓글(5~100자)을 작성합니다."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = CommentRequest.Create.class)
		)
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
		@ApiResponse(
			responseCode = "404",
			description = "댓글 작성 실패 - not found",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorResponse.class),
				examples = {
					@ExampleObject(name = "찾을 수 없는 유저", value = " {\n    \"errorCode\": \"NOT_FOUND_MEMBER\",\n    \"errorMessage\": \"해당 유저가 존재하지 않습니다.\"\n  }"),
					@ExampleObject(name = "찾을 수 없는 리뷰", value = " {\n    \"errorCode\": \"NOT_FOUND_REVIEW\",\n    \"errorMessage\": \"해당 리뷰가 존재하지 않습니다.\"\n  }")
				}
			)
		)
	})
	@PostMapping("/comments")
	public ResponseEntity<Void> create(@RequestBody @Valid CommentRequest.Create commentRequest,
		@AuthenticationPrincipal Member member) {

		commentService.create(member.getId(), commentRequest);

		return ResponseEntity.ok().build();
	}

	@Tag(name = "comment")
	@Operation(
		summary =  "댓글을 수정합니다.",
		description = "선택한 리뷰에 본인이 작성한 댓글(5~100자)을 수정합니다."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = CommentRequest.Modify.class)
		)
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
		@ApiResponse(
			responseCode = "404",
			description = "댓글 수정 실패 - not found",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorResponse.class),
				examples = {
					@ExampleObject(name = "찾을 수 없는 유저", value = " {\n    \"errorCode\": \"NOT_FOUND_MEMBER\",\n    \"errorMessage\": \"해당 유저가 존재하지 않습니다.\"\n  }"),
					@ExampleObject(name = "찾을 수 없는 댓글", value = " {\n    \"errorCode\": \"NOT_FOUND_COMMENT\",\n    \"errorMessage\": \"해당 댓글이 존재하지 않습니다.\"\n  }")
				}
			)
		),
		@ApiResponse(
			responseCode = "403",
			description = "댓글 수정 실패 - forbidden",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorResponse.class),
				examples = {
					@ExampleObject(name = "수정 권한 없음", value = " {\n    \"errorCode\": \"NOT_AUTHORITY_COMMENT\",\n    \"errorMessage\": \"댓글을 수정하거나 삭제할 권한이 없습니다.\"\n  }")
				}
			)
		)
	})
	@PutMapping("/comments/{commentId}")
	public ResponseEntity<Void> modify(
		@Parameter(description = "[댓글 id]", example = "12") @PathVariable Long commentId,
		@RequestBody @Valid CommentRequest.Modify commentRequest,
		@AuthenticationPrincipal Member member) {

		commentService.modify(member.getId(), commentId, commentRequest);

		return ResponseEntity.ok().build();
	}

	@Tag(name = "comment")
	@Operation(
		summary = "댓글을 삭제합니다.",
		description = "선택한 본인이 작성한 댓글을 삭제합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
		@ApiResponse(
			responseCode = "404",
			description = "댓글 삭제 실패 - not found",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorResponse.class),
				examples = {
					@ExampleObject(name = "찾을 수 없는 유저", value = " {\n    \"errorCode\": \"NOT_FOUND_MEMBER\",\n    \"errorMessage\": \"해당 유저가 존재하지 않습니다.\"\n  }"),
					@ExampleObject(name = "찾을 수 없는 댓글", value = " {\n    \"errorCode\": \"NOT_FOUND_COMMENT\",\n    \"errorMessage\": \"해당 댓글이 존재하지 않습니다.\"\n  }")
				}
			)
		),
		@ApiResponse(
			responseCode = "403",
			description = "댓글 삭제 실패 - forbidden",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorResponse.class),
				examples = {
					@ExampleObject(name = "삭제 권한 없음", value = " {\n    \"errorCode\": \"NOT_AUTHORITY_COMMENT\",\n    \"errorMessage\": \"댓글을 수정하거나 삭제할 권한이 없습니다.\"\n  }")
				}
			)
		)
	})
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> delete(
		@Parameter(description = "[댓글 id]", example = "14") @PathVariable Long commentId,
		@Parameter(hidden = true) @AuthenticationPrincipal Member member) {

		commentService.delete(member.getId(), commentId);

		return ResponseEntity.ok().build();
	}

	@Tag(name = "comment")
	@Operation(
		summary = "댓글을 조회합니다.",
		description = "선택한 리뷰의 댓글들을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "댓글 조회 성공",
			content = @Content(
				array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class))
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "댓글 조회 실패 - notfound",
			content = @Content(
				schema = @Schema(implementation = ErrorResponse.class),
				examples = {
					@ExampleObject(name = "존재하지 않는 리뷰", value = " {\n    \"errorCode\": \"NOT_FOUND_REVIEW\",\n    \"errorMessage\": \"해당 리뷰가 존재하지 않습니다.\"\n  }")
				}
			)
		)
	})
	@GetMapping(value = "/reviews/{reviewId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CommentResponse>> list(
		@Parameter(description = "[리뷰 id]", example = "17")
		@PathVariable Long reviewId) {

		return ResponseEntity.ok(commentService.list(reviewId));
	}
}
