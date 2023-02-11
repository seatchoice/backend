package com.example.seatchoice.dto.param;


import com.example.seatchoice.type.AlarmType;
import lombok.Getter;

@Getter
public class AlarmCreateParam {
    private Long memberId;
    private AlarmType type;
    private String url;
}
