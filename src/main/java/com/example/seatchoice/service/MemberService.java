package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.UNSUPPORTED_PROVIDER;
import static com.example.seatchoice.type.LoginType.KAKAO;
import static com.example.seatchoice.type.MemberRole.ROLE_USER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.example.seatchoice.dto.auth.Login;
import com.example.seatchoice.dto.auth.OauthAttribute;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.service.auth.KakaoService;
import com.example.seatchoice.service.auth.TokenService;
import com.example.seatchoice.type.LoginType;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final KakaoService kakaoService;
	private final TokenService tokenService;
	private final RedisTemplate<Long, Object> redisTemplate;

	public Login oauthLogin(String code, String provider) throws IOException, ParseException, CustomException {
		String oauthAccessToken;
		OauthAttribute oauthAttribute;
		Member member;

		if (KAKAO.getProvider().equals(provider)) {
			oauthAccessToken = kakaoService.getToken(code);
			oauthAttribute = kakaoService.getUserInfo(oauthAccessToken);
			member = updateMember(oauthAttribute);
		} else {
			throw new CustomException(UNSUPPORTED_PROVIDER, BAD_REQUEST);
		}

		// redis oauthAccessToken 저장
		HashOperations<Long, Object, Object> hashOperations = redisTemplate.opsForHash();
		hashOperations.put(member.getId(), "oauthAccessToken", oauthAccessToken);

		return Login.builder()
			.accessToken(tokenService.createToken(member))
			.refreshToken(tokenService.createRefreshToken(member))
			.nickname(member.getNickname())
			.build();
	}

	public void oauthLogout(Long memberId, String provider) throws IOException {
		Object oauthAccessToken = redisTemplate.opsForHash().get(memberId, "oauthAccessToken");

		if (oauthAccessToken == null) {
			return;
		}

		if (KAKAO.getProvider().equals(provider)) {
			kakaoService.logout(oauthAccessToken);
			redisTemplate.delete(memberId);
			return;
		}
		throw new CustomException(UNSUPPORTED_PROVIDER, BAD_REQUEST);
	}

	public Member updateMember(OauthAttribute oauthAttribute) {
		LoginType loginType = oauthAttribute.getLoginType();
		String oauthId = oauthAttribute.getOauthId();
		String nickName = oauthAttribute.getNickname();

		Optional<Member> optionalMember = memberRepository.findByLoginTypeAndOauthId(loginType, oauthId);

		Member member;
		if (optionalMember.isEmpty()) {
			member = memberRepository.save(Member.builder()
				.oauthId(oauthId)
				.nickname(nickName)
				.email(oauthAttribute.getEmail())
				.role(ROLE_USER)
				.loginType(loginType)
				.build());
		} else {
			member = optionalMember.get();
		}

		// nickname update
		if (!member.getNickname().equals(nickName)) {
			member.setNickname(nickName);
			memberRepository.save(member);
		}

		return member;
	}
}
