package com.example.seatchoice.dto.cond;

import com.example.seatchoice.entity.Alarm;
import com.example.seatchoice.type.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlarmCond {
    private Long id;
    private AlarmType type;
    private String url;
    private Boolean checkAlarm;

    public static AlarmCond from(Alarm alarm) {
        return AlarmCond.builder()
            .id(alarm.getId())
            .type(alarm.getType())
            .url(alarm.getUrl())
            .checkAlarm(alarm.getCheckAlarm())
            .build();
    }
}
