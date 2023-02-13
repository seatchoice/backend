package com.example.seatchoice.dto.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommentParam {

	@Getter
	@AllArgsConstructor
	public static class Create {
		@NotNull(message = "유저 id를 입력해주세요.")
		private Long memberId;

		@NotNull(message = "필수 입력입니다.")
		private Long reviewId;

		@NotBlank(message = "댓글을 작성해 주세요.")
		private String content;
	}

	@AllArgsConstructor
	@Getter
	public static class Modify {
		@NotNull(message = "유저 id를 입력해주세요.")
		private Long memberId;

		@NotBlank(message = "댓글을 작성해 주세요.")
		private String content;
	}

	@Getter
	@AllArgsConstructor
	public static class Delete {
		@NotNull(message = "유저 id를 입력해주세요.")
		private Long memberId;

	}

}
