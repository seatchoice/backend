package com.example.seatchoice.config.kafka;

import com.example.seatchoice.dto.param.ChattingMessageParam;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSender {

    private final KafkaTemplate<String, ChattingMessageParam> kafkaTemplate;

    public void send(String topic, ChattingMessageParam data) {
        kafkaTemplate.send(topic, data); // send to react clients via websocket(STOMP)
    }
}
