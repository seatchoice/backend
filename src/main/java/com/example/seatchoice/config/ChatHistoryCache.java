package com.example.seatchoice.config;

import com.example.seatchoice.dto.param.ChattingMessageRequest;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ChatHistoryCache {

    private final Cache<UUID, ChattingMessageRequest> chatHistoryCache = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();

    public void save(ChattingMessageRequest chatObj) {
        this.chatHistoryCache.put(UUID.randomUUID(), chatObj);
    }

    public List<ChattingMessageRequest> get(Long roomId) {
        return chatHistoryCache.asMap().values().stream()
            .filter(r -> r.getRoomId().equals(roomId))
            .sorted(Comparator.comparing(ChattingMessageRequest::getTimeStamp))
            .collect(Collectors.toList());
    }
}
