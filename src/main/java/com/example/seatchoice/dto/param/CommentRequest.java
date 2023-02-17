package com.example.seatchoice.dto.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommentRequest {

	@Getter
	@AllArgsConstructor
	public static class Create {

		@NotNull(message = "필수 입력입니다.")
		private Long reviewId;

		@NotBlank
		@Size(min = 5, message = "5자 이상 댓글을 작성해 주세요.")
		private String content;
	}

	@AllArgsConstructor
	@Getter
	public static class Modify {

		@NotBlank
		@Size(min = 5, message = "5자 이상 댓글을 작성해 주세요.")
		private String content;
	}

}
