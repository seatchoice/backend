package com.example.seatchoice.config.oauth;

import static com.example.seatchoice.type.LoginType.KAKAO;

import com.example.seatchoice.type.LoginType;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OAuth2Attribute {
	private String oauthId;
	private String email;
	private String nickname;
	private LoginType loginType;

	public static OAuth2Attribute of(String provider, String userNameAttributeName, Map<String, Object> attributes) {
		switch (provider) {
			case "kakao":
				return ofKakao(attributes, userNameAttributeName);
			default:
				throw new RuntimeException();
		}
	}

	private static OAuth2Attribute ofKakao(Map<String, Object> attributes, String userNameAttributeName) {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

		return OAuth2Attribute.builder()
			.oauthId(String.valueOf(attributes.get(userNameAttributeName)))
			.nickname((String) kakaoProfile.get("nickname"))
			.email((String) kakaoAccount.get("email"))
			.loginType(KAKAO)
			.build();
	}
}

