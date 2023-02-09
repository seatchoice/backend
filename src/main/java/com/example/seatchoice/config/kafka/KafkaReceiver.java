package com.example.seatchoice.config.kafka;

import com.example.seatchoice.dto.param.ChattingMessageParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaReceiver {

    private final SimpMessagingTemplate template;

    @KafkaListener(id = "main-listener", topics = "kafka-chatting")
    public void receive(ChattingMessageParam message) throws Exception {

        HashMap<String, String> msg = new HashMap<>();
        msg.put("nickname", message.getNickname());
        msg.put("message", message.getMessage());
        msg.put("timestamp", message.getTimeStamp());

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(msg);

        this.template.convertAndSend("/api/chat/" + message.getRoomId(), json);
    }
}
