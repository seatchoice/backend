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
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TheaterSeat extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theater_id")
	private Theater theater;

	@NotNull
	private Integer floor;

	private String section;

	@NotNull
	private String seatRow;

	@NotNull
	private Integer number;

	@ColumnDefault("0")
	private Long reviewAmount;

	@ColumnDefault("0.0")
	private Double rating;
}
