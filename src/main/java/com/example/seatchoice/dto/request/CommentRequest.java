package com.example.seatchoice.dto.request;

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
	public static class Create {

		@NotNull(message = "필수 입력입니다.")
		private Long reviewId;

		@NotBlank
		@Size(min = 5, max = 100, message = "5~100자 댓글을 작성해 주세요.")
		private String content;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class Modify {

		@NotBlank
		@Size(min = 5, max = 100, message = "5~100자 댓글을 작성해 주세요.")
		private String content;
	}

}
