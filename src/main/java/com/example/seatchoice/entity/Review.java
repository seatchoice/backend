package com.example.seatchoice.entity;

import com.example.seatchoice.entity.common.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Review extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theater_seat_id")
	private TheaterSeat theaterSeat;

	@NotNull
	private String content;

	private String thumbnailUrl;

	@NotNull
	private Integer rating;

	@ColumnDefault("0")
	private Long likeAmount;

	@ColumnDefault("0")
	private Long commentAmount;

	public void addCommentAmount() {
		if (commentAmount >= 0) {
			this.commentAmount++;
		}
	}

	public void minusCommentAmount() {
		if (commentAmount > 0) {
			this.commentAmount--;
		}
	}
}
