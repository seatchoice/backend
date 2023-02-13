package com.example.seatchoice.service;

import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.service.oauth.OauthService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final OauthService oauthService;

	private final String KAKAO = "kakao";

	public Token oauthLogin(String code, String provider) throws IOException, ParseException {
		switch (provider) {
			case KAKAO:
				return oauthService.kakaoLogin(code);
			default:
				throw new RuntimeException();
		}
	}
}
