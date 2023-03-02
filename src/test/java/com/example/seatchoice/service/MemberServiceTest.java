package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.UNSUPPORTED_PROVIDER;
import static com.example.seatchoice.type.LoginType.KAKAO;
import static com.example.seatchoice.type.MemberRole.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.example.seatchoice.dto.auth.Login;
import com.example.seatchoice.dto.auth.OauthAttribute;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.service.auth.KakaoService;
import com.example.seatchoice.service.auth.TokenService;
import java.io.IOException;
import java.util.Optional;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private KakaoService kakaoService;

	@Mock
	private TokenService tokenService;

	@Mock
	private RedisTemplate<Long, Object> redisTemplate;

	@Mock
	private HashOperations hashOperations;

	@InjectMocks
	private MemberService memberService;

	@Test
	@DisplayName("소셜 로그인 성공")
	void oauthLoginSuccess() throws IOException, ParseException {
		// given
		String code = "testCode";
		String provider = "kakao";
		String oauthAccessToken= "oauthAccessToken";
		OauthAttribute oauthAttribute = OauthAttribute.builder()
			.oauthId("261111")
			.email("test@naver.com")
			.nickname("testNick")
			.loginType(KAKAO)
			.build();

		Member member = Member.builder()
			.oauthId(oauthAttribute.getOauthId())
			.nickname(oauthAttribute.getNickname())
			.email(oauthAttribute.getEmail())
			.role(ROLE_USER)
			.loginType(oauthAttribute.getLoginType())
			.build();

		String accessToken = "testAccessToken";
		String refreshToken = "testRefreshToken";

		given(kakaoService.getToken(anyString())).willReturn(oauthAccessToken);
		given(kakaoService.getUserInfo(anyString())).willReturn(oauthAttribute);
		given(memberRepository.findByLoginTypeAndOauthId(any(), anyString())).willReturn(Optional.ofNullable(member));
		given(redisTemplate.opsForHash()).willReturn(hashOperations);
		given(tokenService.createToken(any())).willReturn(accessToken);
		given(tokenService.createRefreshToken(any())).willReturn(refreshToken);

		// when
		Login login = memberService.oauthLogin(code, provider);

		// then
		assertEquals(login.getAccessToken(), accessToken);
		assertEquals(login.getRefreshToken(), refreshToken);
		assertEquals(login.getNickname(), oauthAttribute.getNickname());
	}

	@Test
	@DisplayName("소셜 로그인 실패 - 지원되지 않는 provider")
	void oauthLoginFailure_unsupportedProvider() {
		// given
		String code = "testCode";
		String provider = "google";

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> memberService.oauthLogin(code, provider));

		// then
		assertEquals(exception.getErrorCode(), UNSUPPORTED_PROVIDER);
		assertEquals(exception.getHttpStatus(), BAD_REQUEST);
	}

	@Test
	@DisplayName("소셜 로그아웃 실패 - 지원되지 않는 provider")
	void oauthLogoutSuccess() throws IOException, ParseException {
		// given
		Long memberId = 100L;
		String provider = "google";
		Object oauthAccessToken = "oauthAccessToken";

		given(redisTemplate.opsForHash()).willReturn(hashOperations);
		given(hashOperations.get(anyLong(), anyString())).willReturn(oauthAccessToken);

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> memberService.oauthLogout(memberId, provider));

		// then
		assertEquals(exception.getErrorCode(), UNSUPPORTED_PROVIDER);
		assertEquals(exception.getHttpStatus(), BAD_REQUEST);

	}
}