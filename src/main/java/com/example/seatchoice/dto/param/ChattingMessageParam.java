package com.example.seatchoice.dto.param;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChattingMessageParam implements Serializable {
    private Long roomId;
    private String nickname;
    private String message;
    private LocalDateTime timeStamp;
}
