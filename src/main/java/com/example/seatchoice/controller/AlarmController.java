package com.example.seatchoice.controller;

import com.example.seatchoice.dto.request.AlarmCreateRequest;
import com.example.seatchoice.dto.response.AlarmResponse;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    // 알림 리스트 가져오기
    @GetMapping("/list")
    public ResponseEntity<Page<AlarmResponse>> getAlarmList(@AuthenticationPrincipal Member member, Pageable pageable) {
        return ResponseEntity.ok(alarmService.getAlarmList(member.getId(), pageable));
    }

    // 알림 조회
    @GetMapping("/{alarmId}")
    public ResponseEntity<AlarmResponse> getAlarm(@PathVariable Long alarmId) {
        return ResponseEntity.ok(alarmService.getAlarm(alarmId));
    }

    // 읽지 않은 알림 전체 읽기
    @GetMapping("/read-all")
    public ResponseEntity<Void> readAllAlarm(@AuthenticationPrincipal Member member) {
        alarmService.readAllAlarm(member.getId());
        return ResponseEntity.ok().build();
    }

    // 알림 생성
    @PostMapping
    public ResponseEntity<AlarmResponse> createAlarm(@RequestBody AlarmCreateRequest alarmCreateRequest) {
        return ResponseEntity.ok(alarmService.createAlarm(alarmCreateRequest.getMemberId(),
                                                          alarmCreateRequest.getType(),
                                                          alarmCreateRequest.getUrl()));
    }

    // 알림 삭제
    @DeleteMapping("/{alarmId}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable Long alarmId) {
        alarmService.deleteAlarm(alarmId);
        return ResponseEntity.ok().build();
    }
}
