package com.example.seatchoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.dto.response.AlarmResponse;
import com.example.seatchoice.entity.Alarm;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.AlarmRepository;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.type.AlarmType;
import com.example.seatchoice.type.ErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {


    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AlarmRepository alarmRepository;

    @InjectMocks
    private AlarmService alarmService;

    @Test
    @DisplayName("알림 리스트 조회 성공")
    void getAlarmListSuccess() {

        // given
        Member member = new Member();
        member.setId(1L);
        List<Alarm> alarms = Arrays.asList(
            Alarm.builder()
                .member(member)
                .type(AlarmType.LIKE)
                .alarmMessage("testUrl1")
                .checkAlarm(false)
                .build(),
            Alarm.builder()
                .member(member)
                .type(AlarmType.COMMENT)
                .alarmMessage("testUrl2")
                .checkAlarm(true)
                .build()
        );

        alarms.get(0).setCreatedAt(LocalDateTime.now());
        alarms.get(1).setCreatedAt(LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        Long lastAlarmId = 2L;

        Slice<AlarmResponse> alarmResponses = new SliceImpl<>(
            Objects.requireNonNull(AlarmResponse.of(alarms)), pageable, true);

        given(alarmRepository.alarmListBySlice(anyLong(), anyLong(), any())).willReturn(alarmResponses);

        // when
        Slice<AlarmResponse> alarmListSlice = alarmService.getAlarmList(lastAlarmId, member.getId(),
            pageable);

        // then
        assertEquals(false, alarmListSlice.getContent().get(0).getCheckAlarm());
        assertEquals(AlarmType.LIKE, alarmListSlice.getContent().get(0).getType());
        assertEquals("testUrl1", alarmListSlice.getContent().get(0).getAlarmMessage());
        assertEquals(true, alarmListSlice.getContent().get(1).getCheckAlarm());
        assertEquals(AlarmType.COMMENT, alarmListSlice.getContent().get(1).getType());
        assertEquals("testUrl2", alarmListSlice.getContent().get(1).getAlarmMessage());
    }

    @Test
    @DisplayName("알림 조회 성공")
    void getAlarmSuccess() {

        // given
        Member member = new Member();
        member.setId(1L);
        Long alarmId = 2L;
        Alarm alarm = Alarm.builder()
            .member(member)
            .checkAlarm(false)
            .alarmMessage("test")
            .type(AlarmType.LIKE)
            .targetMember("유저1")
            .targetReviewId(2L)
            .build();
        alarm.setId(alarmId);
        alarm.setCreatedAt(LocalDateTime.now());

        given(alarmRepository.findById(anyLong())).willReturn(Optional.of(alarm));

        // when
        alarmService.readAlarm(alarmId);

        // then
        assertEquals(true, alarm.getCheckAlarm());
    }

    @Test
    @DisplayName("알림 조회 실패 - 존재하지 않는 알림 ID")
    void getAlarmFailed_NotFoundAlarm() {

        // given
        Long alarmId = 1L;
        given(alarmRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> alarmService.readAlarm(alarmId));

        // then
        assertEquals(ErrorCode.NOT_FOUND_ALARM, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    @DisplayName("알림 전체 읽기 성공")
    void readAllUnreadAlarm() {

        // given
        Member member = new Member();
        member.setId(1L);
        List<Alarm> alarmList = Arrays.asList(
            Alarm.builder()
                .member(member)
                .type(AlarmType.LIKE)
                .alarmMessage("testUrl1")
                .checkAlarm(false)
                .build(),
            Alarm.builder()
                .member(member)
                .type(AlarmType.COMMENT)
                .alarmMessage("testUrl2")
                .checkAlarm(false)
                .build()
        );

        given(alarmRepository.findByMemberId(anyLong())).willReturn(alarmList);

        // when
        alarmService.readAllAlarm(member.getId());

        // then
        verify(alarmRepository, times(1)).saveAll(any());
        assertEquals(true, alarmList.get(0).getCheckAlarm());
        assertEquals(AlarmType.LIKE, alarmList.get(0).getType());
        assertEquals("testUrl1", alarmList.get(0).getAlarmMessage());
        assertEquals(true, alarmList.get(1).getCheckAlarm());
        assertEquals(AlarmType.COMMENT, alarmList.get(1).getType());
        assertEquals("testUrl2", alarmList.get(1).getAlarmMessage());
    }

    @Test
    @DisplayName("알림 생성 성공")
    void createAlarmSuccess() {

        // given
        Long reviewId = 1L;
        String targetMember = "유저1";
        Member member = new Member();
        member.setId(1L);
        AlarmType alarmType = AlarmType.LIKE;
        String alarmMessage = "테스트";
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        // when
        alarmService.createAlarm(member.getId(), alarmType, alarmMessage, reviewId, targetMember);

        // then
        verify(alarmRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("알림 생성 실패 - 존재하지 않는 회원 ID")
    void createAlarmFailed_NotFoundMember() {

        // given
        Long reviewId = 2L;
        String targetMember = "유저1";
        Long memberId = 1L;
        AlarmType alarmType = AlarmType.LIKE;
        String alarmMessage = "테스트";
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> alarmService.createAlarm(memberId, alarmType, alarmMessage, reviewId, targetMember));

        // then
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    @DisplayName("알림 삭제 성공")
    void deleteAlarmSuccess() {

        // given
        Long alarmId = 1L;
        Alarm alarm = new Alarm();
        given(alarmRepository.findById(anyLong())).willReturn(Optional.of(alarm));

        // when
        alarmService.deleteAlarm(alarmId);

        // then
        verify(alarmRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("알림 삭제 실패 - 일치하는 알림 없음")
    void deleteAlarmFailed_NotFoundAlarm() {

        // given
        Long alarmId = 1L;
        given(alarmRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> alarmService.deleteAlarm(alarmId));

        // then
        assertEquals(ErrorCode.NOT_FOUND_ALARM, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }
}