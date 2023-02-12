package com.example.seatchoice.controller;

import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.dto.cond.AlarmCond;
import com.example.seatchoice.dto.param.AlarmCreateParam;
import com.example.seatchoice.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    // 알림 리스트 가져오기
    @GetMapping("/list/{memberId}")
    public ApiResponse<Page<AlarmCond>> getAlarmList(@PathVariable Long memberId, Pageable pageable) {
        return new ApiResponse<>(alarmService.getAlarmList(memberId, pageable));
    }

    // 알림 조회
    @GetMapping("/{alarmId}")
    public ApiResponse<AlarmCond> getAlarm(@PathVariable Long alarmId) {
        return new ApiResponse<>(alarmService.getAlarm(alarmId));
    }

    // 읽지 않은 알림 전체 읽기
    @GetMapping("/read-all/{memberId}")
    public ApiResponse<Void> readAllAlarm(@PathVariable Long memberId) {
        alarmService.readAllAlarm(memberId);
        return new ApiResponse<>();
    }

    // 알림 생성
    @PostMapping
    public ApiResponse<AlarmCond> createAlarm(@RequestBody AlarmCreateParam alarmCreateParam) {
        return new ApiResponse<>(alarmService.createAlarm(alarmCreateParam.getMemberId(),
                                                          alarmCreateParam.getType(),
                                                          alarmCreateParam.getUrl()));
    }

    // 알림 삭제
    @DeleteMapping("/{alarmId}")
    public ApiResponse<Void> deleteAlarm(@PathVariable Long alarmId) {
        alarmService.deleteAlarm(alarmId);
        return new ApiResponse<>();
    }
}
