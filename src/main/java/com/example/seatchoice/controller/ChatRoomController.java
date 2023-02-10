package com.example.seatchoice.controller;

import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.dto.cond.ChatRoomCond;
import com.example.seatchoice.dto.param.TheaterIdParam;
import com.example.seatchoice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 등록
    @PostMapping
    public ApiResponse<ChatRoomCond> createRoom(@RequestBody TheaterIdParam theaterIdParam) {
        ChatRoomCond room = chatRoomService.createRoom(theaterIdParam);
        return new ApiResponse<>(room);
    }

    // 채팅방 삭제
    @DeleteMapping("/{roomId}")
    public ApiResponse<Void> deleteRoom(@PathVariable Long roomId) {
        chatRoomService.deleteRoom(roomId);
        return new ApiResponse<>();
    }
}
