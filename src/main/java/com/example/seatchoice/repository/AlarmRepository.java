package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Alarm;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Page<Alarm> findByMemberId(Long memberId, Pageable pageable);

    List<Alarm> findByMemberId(Long memberId);
}
