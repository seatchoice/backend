package com.example.seatchoice.dto.cond;

import com.example.seatchoice.entity.Comment;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
	private Long id;
	private String content;
	private LocalDate updatedAt;
	private String nickname;

	public static CommentResponse from(Comment comment) {
		return CommentResponse.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.updatedAt(comment.getUpdatedAt().toLocalDate())
			.nickname(comment.getMember().getNickname())
			.build();
	}

}
