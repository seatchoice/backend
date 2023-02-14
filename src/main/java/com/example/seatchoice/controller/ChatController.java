package com.example.seatchoice.controller;

import com.example.seatchoice.config.kafka.KafkaSender;
import com.example.seatchoice.dto.param.ChattingMessageParam;
import com.example.seatchoice.service.oauth.TokenService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final KafkaSender kafkaSender;
    private final TokenService tokenService;
    private static String KAFKA_TOPIC = "kafka-chatting"; // kafka 내부 topic

    // "/api/send/{roomId}"로 들어오는 메시지를 "/api/chat/{roomId}"을 구독하고있는 사람들에게 송신
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId,
//                            @Header("Authorization") String token,
                            ChattingMessageParam message) {

        // 현재 시간
        String sendTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .format(LocalDateTime.now());

        message.setRoomId(roomId);
//        message.setNickname(tokenService.getNickname(token).toString());
        message.setTimeStamp(sendTime);

        kafkaSender.send(KAFKA_TOPIC, message);
    }
}
