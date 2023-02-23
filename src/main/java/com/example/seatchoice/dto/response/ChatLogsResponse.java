package com.example.seatchoice.dto.response;


import com.example.seatchoice.dto.request.ChattingMessageRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatLogsResponse {
    private String nickname;
    private List<ChattingMessageRequest> chattingMessages;
}
