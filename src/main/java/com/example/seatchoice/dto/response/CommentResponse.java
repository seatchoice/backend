package com.example.seatchoice.dto.response;

import com.example.seatchoice.entity.Comment;
import com.example.seatchoice.entity.Member;
import java.time.format.DateTimeFormatter;
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
	private String updatedAt;
	private String nickname;

	private Long userId;

	public static CommentResponse from(Comment comment) {
		Member member = comment.getMember();
		return CommentResponse.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.updatedAt(comment.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
			.nickname(member.getNickname())
			.userId(member.getId())
			.build();
	}

}
