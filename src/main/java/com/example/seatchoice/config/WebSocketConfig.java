package com.example.seatchoice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/api/chat-room");       //클라이언트에서 보낸 메세지를 받을 prefix
        registry.enableSimpleBroker("/api/topic/chat-room");    //해당 주소를 구독하고 있는 클라이언트들에게 메세지 전달
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat") //SockJS 연결 주소
            .setAllowedOriginPatterns("*")
            .withSockJS(); // 리액트 연동시에는 주석 해제. 브라우저 테스트 시에는 주석 처리
        // 주소 : ws://localhost:8080/chat
    }
}
