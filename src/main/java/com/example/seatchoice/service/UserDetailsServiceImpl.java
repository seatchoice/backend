package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
		Member member = memberRepository.findById(Long.valueOf(memberId))
			.orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER, NOT_FOUND));

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(member.getRole().toString()));

		return new User(member.getId().toString(), member.getNickname(), grantedAuthorities);
	}
}
