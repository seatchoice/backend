package com.example.seatchoice.controller;

import com.example.seatchoice.config.ChatHistory;
import com.example.seatchoice.dto.param.ChattingMessageParam;
import com.example.seatchoice.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final RabbitTemplate rabbitTemplate;
    private final String CHAT_EXCHANGE_NAME = "chat.exchange";
    private final ChatHistory chatHistory;

    @MessageMapping("chat.message.{roomId}")
    public void sendMessage(@DestinationVariable Long roomId,
                            @AuthenticationPrincipal Member member,
                            ChattingMessageParam message) {

        message.setRoomId(roomId);
        message.setNickname(member.getNickname());
        message.setTimeStamp(LocalDateTime.now());
        chatHistory.save(message);

        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + roomId, message);
    }

    @GetMapping("/api/chat-room/{roomId}")
    public List<ChattingMessageParam> getChattingHistory(@PathVariable Long roomId) {
        return chatHistory.get(roomId);
    }
}
