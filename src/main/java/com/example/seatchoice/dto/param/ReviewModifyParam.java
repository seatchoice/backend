package com.example.seatchoice.dto.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModifyParam {
	@NotBlank(message = "필수 입력입니다.")
	@Size(min = 1, max = 300, message = "리뷰내용은 1~300자로 내로 입력하세요.")
	private String content;

	private Integer rating;
}
