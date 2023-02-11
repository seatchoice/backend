package com.example.seatchoice.config.oauth;

import com.example.seatchoice.type.LoginType;
import java.util.Map;

public class OAuth2MemberInfoFactory {
    public static OAuth2MemberInfo getOAuth2UserInfo(LoginType loginType, Map<String, Object> attributes) {
        switch (loginType) {
            case KAKAO: return new KakaoOAuth2MemberInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
