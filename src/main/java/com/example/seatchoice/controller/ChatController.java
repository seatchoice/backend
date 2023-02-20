package com.example.seatchoice.controller;

import static com.example.seatchoice.type.ErrorCode.INVALID_TOKEN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.example.seatchoice.config.ChatHistoryCache;
import com.example.seatchoice.dto.request.ChattingMessageRequest;
import com.example.seatchoice.entity.Member;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.service.auth.TokenService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
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
    private final ChatHistoryCache chatHistoryCache;
    private final TokenService tokenService;

    @MessageMapping("chat.message.{roomId}")
    public void sendMessage(@DestinationVariable Long roomId,
                            @Header("Authorization") String token,
                            ChattingMessageRequest message) {

        if (!tokenService.validateToken(token)) {
            throw new CustomException(INVALID_TOKEN, BAD_REQUEST);
        }

        message.setRoomId(roomId);
        message.setTimeStamp(LocalDateTime.now());
        chatHistoryCache.save(message);

        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + roomId, message);
    }

    @GetMapping("/api/chat-history/{roomId}")
    public List<ChattingMessageRequest> getChattingHistory(@PathVariable Long roomId) {
        return chatHistoryCache.get(roomId);
    }
}
