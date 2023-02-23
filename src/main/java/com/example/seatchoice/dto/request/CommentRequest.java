package com.example.seatchoice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentRequest {

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Schema(name = "댓글 작성 요청")
	public static class Create {

		@NotNull(message = "필수 입력입니다.")
		@Schema(description = "리뷰 id", example = "12")
		private Long reviewId;

		@NotBlank
		@Size(min = 5, max = 100, message = "5~100자 댓글을 작성해 주세요.")
		@Schema(description = "댓글 내용", example = "이 자리 정말 좋네요.")
		private String content;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Schema(name = "댓글 수정 요청")
	public static class Modify {

		@NotBlank
		@Size(min = 5, max = 100, message = "5~100자 댓글을 작성해 주세요.")
		@Schema(description = "댓글 내용", example = "이 자리 정말 좋네요.")
		private String content;
	}

}
