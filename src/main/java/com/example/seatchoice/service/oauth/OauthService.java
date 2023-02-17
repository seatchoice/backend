package com.example.seatchoice.service.oauth;

import static com.example.seatchoice.type.MemberRole.ROLE_USER;

import com.example.seatchoice.dto.auth.OauthAttribute;
import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.repository.MemberRepository;
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
public class OauthService {

	private final MemberRepository memberRepository;
	private final KakaoService kakaoService;
	private final TokenService tokenService;
	private final RedisTemplate<Long, Object> redisTemplate;

	private final String REDIS_ACCESS_TOKEN_KEY = "accessToken";

	public Token kakaoLogin(String code) throws IOException, ParseException {
		String kakaoAccessToken = kakaoService.getToken(code);
		OauthAttribute oauthAttribute = kakaoService.getUserInfo(kakaoAccessToken);

		Member member = updateMember(oauthAttribute);

		// redis 카카오 엑세스토큰 저장 (로그아웃 시 필요합니다.)
		HashOperations<Long, Object, Object> hashOperations = redisTemplate.opsForHash();
		hashOperations.put(member.getId(), REDIS_ACCESS_TOKEN_KEY, kakaoAccessToken);

		String accessToken = tokenService.createToken(member);
		String refreshToken = tokenService.createRefreshToken(member);
		return new Token(accessToken, refreshToken);
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
