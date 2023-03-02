package com.example.seatchoice.dto.response;

import com.example.seatchoice.entity.Alarm;
import com.example.seatchoice.type.AlarmType;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlarmResponse {
    private Long id;
    private AlarmType type;
    private String alarmMessage;
    private Long targetId;
    private String targetMember;
    private Boolean checkAlarm;
    private String createdAt;

    public static AlarmResponse from(Alarm alarm) {
        return AlarmResponse.builder()
            .id(alarm.getId())
            .type(alarm.getType())
            .alarmMessage(alarm.getAlarmMessage())
            .targetId(alarm.getTargetReviewId())
            .targetMember(alarm.getTargetMember())
            .checkAlarm(alarm.getCheckAlarm())
            .createdAt(alarm.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .build();
    }

    public static List<AlarmResponse> of(List<Alarm> alarms) {
        if (CollectionUtils.isEmpty(alarms)) {
            return Collections.emptyList();
        }

        return alarms.stream()
            .map(AlarmResponse::from)
            .collect(Collectors.toList());
    }
}
