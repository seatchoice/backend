package com.example.seatchoice.entity;

import com.example.seatchoice.entity.common.BaseEntity;
import com.example.seatchoice.type.LoginType;
import com.example.seatchoice.type.MemberRole;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member extends BaseEntity {

	private String qauthId;
	private String nickname;
	private String email;

	@NotNull
	@Enumerated(EnumType.STRING)
	private MemberRole role;

	@NotNull
	@Enumerated(EnumType.STRING)
	private LoginType loginType;
}
