package com.example.seatchoice.service;

import static com.example.seatchoice.type.LoginType.KAKAO;
import static com.example.seatchoice.type.MemberRole.ROLE_USER;

import com.example.seatchoice.config.jwt.JwtTokenProvider;
import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.dto.auth.UserInfo;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.repository.MemberRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
	private final KakaoService kakaoService;

	public Token oAuthLogin(String code) throws IOException, ParseException {
		String accessToken = kakaoService.getToken(code);
		UserInfo userInfo = kakaoService.getUserInfo(accessToken);
		String oauthId = userInfo.getOauthId();
		String nickname = userInfo.getNickname();

		Optional<Member> optionalMember = memberRepository.findByOauthId(oauthId);
		Member member;
		if (optionalMember.isEmpty()) {
			member = memberRepository.save(Member.builder()
				.oauthId(oauthId)
				.nickname(nickname)
				.email(userInfo.getEmail())
				.role(ROLE_USER)
				.loginType(KAKAO)
				.build());
		} else {
			member = optionalMember.get();
		}

		if (!member.getNickname().equals(nickname)) {
			member.setNickname(nickname);
			memberRepository.save(member);
		}

		List<String> roles = new ArrayList<>();
		roles.add(member.getRole().toString());

		return jwtTokenProvider.createToken(member.getId(), roles);
	}
}
