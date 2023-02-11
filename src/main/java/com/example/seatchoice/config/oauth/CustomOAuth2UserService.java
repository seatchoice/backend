package com.example.seatchoice.config.oauth;

import static com.example.seatchoice.type.MemberRole.ROLE_USER;

import com.example.seatchoice.entity.Member;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.type.LoginType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final RedisTemplate<Long, Object> redisTemplate;
	private final MemberRepository memberRepository;

	private final String ACCESS_TOKEN = "accessToken";

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);
		OAuth2AccessToken oAuth2AccessToken = userRequest.getAccessToken();

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String userNameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();

		OAuth2Attribute oAuth2Attribute =
			OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

		Member member = updateMember(oAuth2Attribute);

		//redis accessToken 저장
		String accessToken = oAuth2AccessToken.getTokenType().getValue() + " " + oAuth2AccessToken.getTokenValue();
		HashOperations<Long, Object, Object> hashOperations = redisTemplate.opsForHash();
		hashOperations.put(member.getId(), ACCESS_TOKEN, accessToken);

		Map<String, Object> attribute = new HashMap<>();
		attribute.put("id", member.getId());
		attribute.put("nickname", member.getNickname());

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())), attribute, "id");
	}

	private Member updateMember(OAuth2Attribute oAuth2Attribute) {
		LoginType loginType = oAuth2Attribute.getLoginType();
		String oauthId = oAuth2Attribute.getOauthId();
		String nickName = oAuth2Attribute.getNickname();

		Optional<Member> optionalMember = memberRepository.findByLoginTypeAndOauthId(loginType, oauthId);

		Member member;
		if (optionalMember.isEmpty()) {
			member = memberRepository.save(Member.builder()
				.oauthId(oauthId)
				.nickname(nickName)
				.email(oAuth2Attribute.getEmail())
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
