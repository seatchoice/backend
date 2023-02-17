package com.example.seatchoice.service.oauth;

import static com.example.seatchoice.type.LoginType.KAKAO;
import static com.example.seatchoice.type.MemberRole.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.example.seatchoice.dto.auth.OauthAttribute;
import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.repository.MemberRepository;
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
class OauthServiceTest {

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
	private OauthService oauthService;

	@Test
	@DisplayName("kakao 로그인 성공")
	void kakaoLoginSuccess() throws IOException, ParseException {
		// given
		String code = "testCode";
		String kakaoAccessToken = "Bearer testAcessToken";
		OauthAttribute oauthAttribute = OauthAttribute.builder()
			.oauthId("12345")
			.email(null)
			.nickname("정아")
			.loginType(KAKAO)
			.build();

		Member member = Member.builder()
			.oauthId(oauthAttribute.getOauthId())
			.nickname(oauthAttribute.getNickname())
			.email(oauthAttribute.getEmail())
			.role(ROLE_USER)
			.loginType(oauthAttribute.getLoginType())
			.build();
		member.setId(6L);

		String accessToken = "testAccessToken";
		String refreshToken = "testRefreshToken";

		given(kakaoService.getToken(anyString())).willReturn(kakaoAccessToken);
		given(kakaoService.getUserInfo(anyString())).willReturn(oauthAttribute);
		given(tokenService.createToken(any())).willReturn(accessToken);
		given(tokenService.createRefreshToken(any())).willReturn(refreshToken);
		given(memberRepository.findByLoginTypeAndOauthId(any(), anyString())).willReturn(Optional.of(member));
		given(redisTemplate.opsForHash()).willReturn(hashOperations);

		// when
		Token token = oauthService.kakaoLogin(code);

		// then
		assertEquals(token.getAccessToken(), accessToken);
		assertEquals(token.getRefreshToken(), refreshToken);
	}

	@Test
	@DisplayName("member 업데이트 성공 - 최초 회원가입")
	void updateMemberSuccess_firstTime() {
		// given
		OauthAttribute oauthAttribute = OauthAttribute.builder()
			.oauthId("12345")
			.email(null)
			.nickname("정아")
			.loginType(KAKAO)
			.build();

		Member member = Member.builder()
			.oauthId(oauthAttribute.getOauthId())
			.nickname(oauthAttribute.getNickname())
			.email(oauthAttribute.getEmail())
			.role(ROLE_USER)
			.loginType(oauthAttribute.getLoginType())
			.build();
		member.setId(6L);

		given(memberRepository.findByLoginTypeAndOauthId(any(), anyString())).willReturn(Optional.empty());
		given(memberRepository.save(any())).willReturn(member);

		// when
		Member resultMember = oauthService.updateMember(oauthAttribute);

		// then
		assertEquals(resultMember.getId(), 6L);
		assertEquals(resultMember.getOauthId(), "12345");
		assertEquals(resultMember.getNickname(), "정아");
		assertEquals(resultMember.getEmail(), null);
		assertEquals(resultMember.getLoginType(), KAKAO);
	}

	@Test
	@DisplayName("member 업데이트 성공 - 기존 멤버")
	void updateMemberSuccess_exist() {
		// given
		OauthAttribute oauthAttribute = OauthAttribute.builder()
			.oauthId("12345")
			.email(null)
			.nickname("정아")
			.loginType(KAKAO)
			.build();

		Member member = Member.builder()
			.oauthId(oauthAttribute.getOauthId())
			.nickname(oauthAttribute.getNickname())
			.email(oauthAttribute.getEmail())
			.role(ROLE_USER)
			.loginType(oauthAttribute.getLoginType())
			.build();
		member.setId(6L);

		given(memberRepository.findByLoginTypeAndOauthId(any(), anyString())).willReturn(Optional.of(member));

		// when
		Member resultMember = oauthService.updateMember(oauthAttribute);

		// then
		assertEquals(resultMember.getId(), 6L);
		assertEquals(resultMember.getOauthId(), "12345");
		assertEquals(resultMember.getNickname(), "정아");
		assertEquals(resultMember.getEmail(), null);
		assertEquals(resultMember.getLoginType(), KAKAO);
	}
}