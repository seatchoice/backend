package com.example.seatchoice.config.oauth;

import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.seatchoice.type.ErrorCode.WRONG_SOCIAL_LOGIN_TYPE;

import com.example.seatchoice.config.jwt.UserPrincipal;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.type.LoginType;
import com.example.seatchoice.type.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        LoginType loginType = LoginType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2MemberInfo userInfo = OAuth2MemberInfoFactory.getOAuth2UserInfo(loginType, user.getAttributes());
        Member savedMember = memberRepository.findByOauthId(userInfo.getId());

        if (savedMember != null) {
            if (loginType != savedMember.getLoginType()) {
                throw new CustomException(WRONG_SOCIAL_LOGIN_TYPE, HttpStatus.BAD_REQUEST);
            }
            updateUser(savedMember, userInfo);
        } else {
            savedMember = createUser(userInfo, loginType);
        }

        return UserPrincipal.create(savedMember, user.getAttributes());
    }

    private Member createUser(OAuth2MemberInfo userInfo, LoginType loginType) {
        Member member = new Member (
            userInfo.getId(),
            userInfo.getName(),
            userInfo.getEmail(),
            MemberRole.USER,
            loginType
        );

        return memberRepository.saveAndFlush(member);
    }

    private Member updateUser(Member member, OAuth2MemberInfo userInfo) {
        if (userInfo.getName() != null && !member.getNickname().equals(userInfo.getName())) {
            member.setNickname(userInfo.getName());
        }

        return member;
    }
}
