package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.UNSUPPORTED_PROVIDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.service.auth.OauthService;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private OauthService oauthService;

	@InjectMocks
	private MemberService memberService;

	@Test
	@DisplayName("소셜 로그인 성공")
	void oauthLoginSuccess() throws IOException, ParseException {
		// given
		String code = "testCode";
		String provider = "kakao";
		Token token = new Token("accessToken", "refreshToken");
		given(oauthService.kakaoLogin(anyString())).willReturn(token);

		// when
		Token resultToken = memberService.oauthLogin(code, provider);

		// then
		assertEquals(resultToken, token);
	}

	@Test
	@DisplayName("소셜 로그인 실패 - 지원되지 않는 provider")
	void oauthLoginFailure_unsupportedProvider() {
		// given
		String code = "testCode";
		String provider = "google";
		Token token = new Token("accessToken", "refreshToken");

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> memberService.oauthLogin(code, provider));

		// then
		assertEquals(exception.getErrorCode(), UNSUPPORTED_PROVIDER);
		assertEquals(exception.getHttpStatus(), BAD_REQUEST);
	}
}