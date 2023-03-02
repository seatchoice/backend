package com.example.seatchoice.controller;

import com.example.seatchoice.config.ChatHistoryCache;
import com.example.seatchoice.dto.request.ChattingMessageRequest;
import com.example.seatchoice.dto.response.ChatLogsResponse;
import com.example.seatchoice.entity.Member;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
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
    private final ChatHistoryCache chatHistoryCache;

    @MessageMapping("chat.message.{roomId}")
    public void sendMessage(@DestinationVariable Long roomId,
                            ChattingMessageRequest message) {

        message.setRoomId(roomId);
        message.setTimeStamp(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        chatHistoryCache.save(message);

        String CHAT_EXCHANGE_NAME = "chat.exchange";

        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + roomId, message);
    }

    @GetMapping("/api/chat-history/{roomId}")
    public ResponseEntity<ChatLogsResponse> getChattingHistory(@PathVariable Long roomId,
                                                               @AuthenticationPrincipal Member member) {
        ChatLogsResponse chatLogsResponse = new ChatLogsResponse();
        chatLogsResponse.setNickname(member.getNickname());

        List<ChattingMessageRequest> chattingMessageRequests = chatHistoryCache.get(roomId);
        chatLogsResponse.setChattingMessages(chattingMessageRequests);

        return ResponseEntity.ok(chatLogsResponse);
    }
}
