package com.example.seatchoice.service;

import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_CHATROOM;
import static com.example.seatchoice.type.ErrorCode.NOT_FOUND_THEATER;

import com.example.seatchoice.dto.common.ErrorResponse;
import com.example.seatchoice.dto.cond.ChatRoomCond;
import com.example.seatchoice.entity.ChatRoom;
import com.example.seatchoice.entity.Theater;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.ChatRoomRepository;
import com.example.seatchoice.repository.TheaterRepository;
import com.example.seatchoice.type.ErrorCode;
import javax.lang.model.type.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final TheaterRepository theaterRepository;

    // 방만들기
    public ChatRoomCond createRoom(Long theaterId) {

        if (!chatRoomRepository.existsByTheater_Id(theaterId)) {

            Theater theater = theaterRepository.findById(theaterId).orElseThrow(
                () -> new CustomException(NOT_FOUND_THEATER, HttpStatus.NOT_FOUND));

            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setTheater(theater);
            chatRoomRepository.save(chatRoom);

            return ChatRoomCond.from(chatRoom);

        } else {
            return ChatRoomCond.from(
                chatRoomRepository.findByTheater_Id(theaterId).orElseThrow(
                    () -> new CustomException(NOT_FOUND_CHATROOM, HttpStatus.NOT_FOUND)));
        }
    }

    // 방 삭제
    @Transactional
    public void deleteRoom(Long roomId) {
        chatRoomRepository.deleteById(roomId);
    }
}
