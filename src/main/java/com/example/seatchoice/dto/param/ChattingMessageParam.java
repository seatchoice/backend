package com.example.seatchoice.dto.param;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChattingMessageParam implements Serializable {
    private Long roomId;
    private String nickname;
    private String message;
    private String timeStamp;
}
