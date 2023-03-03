package com.example.seatchoice.repository;

import com.example.seatchoice.entity.Alarm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long>, AlarmRepositoryCustom {
    List<Alarm> findByMemberId(Long memberId);
}
