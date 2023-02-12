package com.example.seatchoice.dto.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewParam {

	@NotNull(message = "필수 입력입니다.")
	@Positive(message = "1층 이상을 입력해주세요")
	private Integer floor;

	private String section;

	@NotBlank(message = "필수 입력입니다.")
	private String seatRow;

	@NotNull(message = "필수 입력입니다.")
	private Integer seatNumber;

	@NotBlank(message = "필수 입력입니다.")
	@Pattern(regexp = "^.{1,300}$", message = "리뷰내용은 1~300자로 내로 입력하세요.")
	private String content;

	private Integer rating;
}
