package com.example.seatchoice.dto.response;

import com.example.seatchoice.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "댓글 조회 응답")
public class CommentResponse {

	@Schema(description = "댓글 id", example = "1")
	private Long id;
	@Schema(description = "댓글 내용", example = "와, 정말 멋진 자리네요.")
	private String content;
	@Schema(description = "수정 날짜", example = "2023-02-22 17-00-23")
	private String updatedAt;
	@Schema(description = "닉네임", example = "코카콜라제로")
	private String nickname;

	public static CommentResponse from(Comment comment) {
		return CommentResponse.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.updatedAt(comment.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")))
			.nickname(comment.getMember().getNickname())
			.build();
	}

}
