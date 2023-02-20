package com.example.seatchoice.dto.request;


import com.example.seatchoice.type.AlarmType;
import lombok.Getter;

@Getter
public class AlarmCreateRequest {
    private Long memberId;
    private AlarmType type;
    private String url;
}
