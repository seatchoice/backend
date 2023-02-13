package com.example.seatchoice.dto.param;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommentParam {

	@Getter
	@AllArgsConstructor
	public static class Create {

		@NotNull(message = "필수 입력입니다.")
		private Long reviewId;

		@Pattern(regexp = "^\\S{5,}", message = "공백으로 시작하지 않고, 5자 이상 댓글을 작성해 주세요.")
		private String content;
	}

	@AllArgsConstructor
	@Getter
	public static class Modify {

		@Pattern(regexp = "^\\S{5,}", message = "공백으로 시작하지 않고, 5자 이상 댓글을 작성해 주세요.")
		private String content;
	}

}
