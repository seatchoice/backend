package com.example.seatchoice.config.jwt;

import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;

import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByOauthId(username);
        if (member == null) {
            throw new CustomException(NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND);
        }
        return UserPrincipal.create(member);
    }
}
