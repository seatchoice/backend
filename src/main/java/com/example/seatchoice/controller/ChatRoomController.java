package com.example.seatchoice.controller;

import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.dto.cond.ChatRoomCond;
import com.example.seatchoice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 등록
    @PostMapping("/{theaterId}")
    public ApiResponse<ChatRoomCond> createRoom(@PathVariable Long theaterId) {
        ChatRoomCond room = chatRoomService.createRoom(theaterId);
        return new ApiResponse<>(room);
    }

    // 채팅방 삭제
    @DeleteMapping("/{roomId}")
    public ApiResponse<Void> deleteRoom(@PathVariable Long roomId) {
        chatRoomService.deleteRoom(roomId);
        return new ApiResponse<>();
    }
}
