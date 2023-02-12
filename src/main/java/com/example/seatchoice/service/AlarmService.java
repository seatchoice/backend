package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_ALARM;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;

import com.example.seatchoice.dto.cond.AlarmCond;
import com.example.seatchoice.entity.Alarm;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.AlarmRepository;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.type.AlarmType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    // 알림 리스트 조회
    public Page<AlarmCond> getAlarmList(Long memberId, Pageable pageable) {
        Page<Alarm> alarmList = alarmRepository.findByMemberId(memberId, pageable);
        return alarmList.map(AlarmCond::from);
    }

    // 알림 조회 - 조회시 읽음 처리
    public AlarmCond getAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(
            () -> new CustomException(NOT_FOUND_ALARM, HttpStatus.NOT_FOUND));
        alarm.setCheckAlarm(true);
        alarmRepository.save(alarm);
        return AlarmCond.from(alarm);
    }

    // 읽지 않은 알림 전체 읽기
    public void readAllAlarm(Long memberId) {
        List<Alarm> alarmList = alarmRepository.findByMemberId(memberId);
        List<Alarm> readAlarmList = new ArrayList<>();
        for (Alarm alarm : alarmList) {
            if (!alarm.getCheckAlarm()) {
                alarm.setCheckAlarm(true);
            }
            readAlarmList.add(alarm);
        }
        alarmRepository.saveAll(readAlarmList);
    }

    // 알림 생성
    public AlarmCond createAlarm(Long memberId, AlarmType alarmType, String url) {
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new CustomException(NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND));
        Alarm alarm = new Alarm(member, alarmType, url, false);
        alarmRepository.save(alarm);

        return AlarmCond.from(alarm);
    }

    // 알림 삭제
    @Transactional
    public void deleteAlarm(Long alarmId) {
        alarmRepository.delete(
            alarmRepository.findById(alarmId).orElseThrow(
                () -> new CustomException(NOT_FOUND_ALARM, HttpStatus.NOT_FOUND)));
    }
}