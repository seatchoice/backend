package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_ALARM;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.seatchoice.type.ErrorCode.WRONG_ALARM_TYPE;

import com.example.seatchoice.dto.response.AlarmResponse;
import com.example.seatchoice.entity.Alarm;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.AlarmRepository;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.type.AlarmType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    // 알림 리스트 조회
    public Slice<AlarmResponse> getAlarmList(Long lastAlarmId, Long memberId, Pageable pageable) {
        return alarmRepository.alarmListBySlice(lastAlarmId, memberId, pageable);
    }

    // 알림 조회 - 조회시 읽음 처리
    public void readAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(
            () -> new CustomException(NOT_FOUND_ALARM, HttpStatus.NOT_FOUND));
        alarm.setCheckAlarm(true);
        alarmRepository.save(alarm);
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
    @Transactional
    public void createAlarm(Long memberId, AlarmType alarmType, String alarmMessage, Long targetReviewId, String targetMember) {
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new CustomException(NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND));

        if (alarmType == AlarmType.COMMENT) {
            Alarm alarm = new Alarm(member, alarmType, alarmMessage, targetReviewId, targetMember, false);
            alarmRepository.save(alarm);

        } else if (alarmType == AlarmType.LIKE) {
            Alarm alarm = new Alarm(member, alarmType, targetMember + "님이 좋아요를 눌렀습니다.", targetReviewId, targetMember, false);
            alarmRepository.save(alarm);

        } else {
            throw new CustomException(WRONG_ALARM_TYPE, HttpStatus.BAD_REQUEST);
        }
    }

    // 알림 삭제
    @Transactional
    public void deleteAlarm(Long alarmId) {
        alarmRepository.delete(
            alarmRepository.findById(alarmId).orElseThrow(
                () -> new CustomException(NOT_FOUND_ALARM, HttpStatus.NOT_FOUND)));
    }
}
