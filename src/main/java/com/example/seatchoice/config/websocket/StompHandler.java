package com.example.seatchoice.config.websocket;


import static com.example.seatchoice.type.ErrorCode.EMPTY_TOKEN;
import static com.example.seatchoice.type.ErrorCode.INVALID_TOKEN;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_MEMBER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.MemberRepository;
import com.example.seatchoice.service.auth.TokenService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {

            try {
                String token = headerAccessor.getFirstNativeHeader("Authorization");
                if (tokenService.validateToken(token)) {
                    Long memberId = tokenService.getMemberId(token);
                    memberRepository.findById(memberId).orElseThrow(
                        () -> new CustomException(NOT_FOUND_MEMBER, NOT_FOUND));
                } else if (!tokenService.validateToken(token)) {
                    throw new CustomException(INVALID_TOKEN, UNAUTHORIZED);
                }

            } catch (SignatureException | MalformedJwtException e) {
                throw new CustomException(INVALID_TOKEN, UNAUTHORIZED);
            } catch (IllegalArgumentException e) {
                throw new CustomException(EMPTY_TOKEN, BAD_REQUEST);
            }
        }

        return message;
    }
}
