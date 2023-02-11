package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Member;
import com.example.seatchoice.type.LoginType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByLoginTypeAndOauthId(LoginType loginType, String oauthId);
}
