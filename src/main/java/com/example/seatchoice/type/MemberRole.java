package com.example.seatchoice.type;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {

	USER("ROLE_USER", "일반 사용자 권한"),
	ADMIN("ROLE_ADMIN", "관리자 권한"),
	GUEST("GUEST", "게스트 권한");

	private final String code;
	private final String displayName;

	public static MemberRole of(String code) {
		return Arrays.stream(MemberRole.values())
			.filter(r -> r.getCode().equals(code))
			.findAny()
			.orElse(GUEST);
	}
}
