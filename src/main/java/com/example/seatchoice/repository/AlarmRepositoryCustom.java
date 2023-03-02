package com.example.seatchoice.repository;

import com.example.seatchoice.dto.response.AlarmResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AlarmRepositoryCustom {
    Slice<AlarmResponse> alarmListBySlice(Long lastAlarmId, Long memberId, Pageable pageable);
}
