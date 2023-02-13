package com.example.seatchoice.dto.cond;

import com.example.seatchoice.entity.Comment;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCond {
	private Long id;
	private String content;
	private LocalDate updatedAt;
	private String nickname;

	public static CommentCond from(Comment comment) {
		return CommentCond.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.updatedAt(comment.getUpdatedAt().toLocalDate())
			.nickname(comment.getMember().getNickname())
			.build();
	}

}
