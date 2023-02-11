package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Member findByOauthId(String oauthId);
}
