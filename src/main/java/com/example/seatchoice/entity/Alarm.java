package com.example.seatchoice.entity;

import com.example.seatchoice.entity.common.BaseEntity;
import com.example.seatchoice.type.AlarmType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Alarm extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Enumerated(EnumType.STRING)
	private AlarmType type;

	@NotNull
	private String alarmMessage;

	@NotNull
	private Long targetId;

	@NotNull
	private Long madeBy;

	@NotNull
	private Boolean checkAlarm;
}
