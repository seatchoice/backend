package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_CHATROOM;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_THEATER;

import com.example.seatchoice.dto.cond.ChatRoomCond;
import com.example.seatchoice.dto.param.TheaterIdParam;
import com.example.seatchoice.entity.ChatRoom;
import com.example.seatchoice.entity.Theater;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.ChatRoomRepository;
import com.example.seatchoice.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final TheaterRepository theaterRepository;

    // 방만들기 검증
    public ChatRoomCond createRoom(TheaterIdParam theaterIdParam) {
        return ChatRoomCond.from(
            chatRoomRepository.findByTheaterId(theaterIdParam.getTheaterId()).orElseGet(
                () -> makeNewChatRoom(theaterIdParam.getTheaterId())));
    }

    // 방 삭제
    @Transactional
    public void deleteRoom(Long roomId) {
        chatRoomRepository.delete(
            chatRoomRepository.findById(roomId).orElseThrow(
                () -> new CustomException(NOT_FOUND_CHATROOM, HttpStatus.NOT_FOUND)));
    }

    // 방만들기
    public ChatRoom makeNewChatRoom(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(
                () -> new CustomException(NOT_FOUND_THEATER, HttpStatus.NOT_FOUND));

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setTheater(theater);
        chatRoomRepository.save(chatRoom);

        return chatRoom;
    }
}
