package com.example.seatchoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.seatchoice.dto.response.ChatRoomResponse;
import com.example.seatchoice.dto.request.TheaterIdRequest;
import com.example.seatchoice.entity.ChatRoom;
import com.example.seatchoice.entity.Theater;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.repository.ChatRoomRepository;
import com.example.seatchoice.repository.TheaterRepository;
import com.example.seatchoice.type.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private TheaterRepository theaterRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    @DisplayName("채팅방 생성 성공 - 방이 없어서 새로 만드는 경우")
    void createRoomSuccess_makeNewRoom() {

        // given
        TheaterIdRequest theaterIdRequest = new TheaterIdRequest();
        theaterIdRequest.setTheaterId(1L);
        Theater theater = new Theater();

        given(theaterRepository.findById(anyLong())).willReturn(Optional.of(theater));

        // when
        chatRoomService.makeNewChatRoom(theaterIdRequest.getTheaterId());

        // then
        verify(chatRoomRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("채팅방 생성 성공 - 방이 있어서 그 값을 리턴 하는 경우")
    void createRoomSuccess_returnRoomInfo() {

        // given
        TheaterIdRequest theaterIdRequest = new TheaterIdRequest();
        theaterIdRequest.setTheaterId(1L);
        Theater theater = new Theater();
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setTheater(theater);
        chatRoom.setId(2L);


        given(chatRoomRepository.findByTheaterId(anyLong())).willReturn(Optional.of(chatRoom));

        // when
        ChatRoomResponse room = chatRoomService.createRoom(theaterIdRequest);

        // then
        verify(chatRoomRepository, times(0)).save(any());
        assertEquals(2, room.getRoomId());
    }

    @Test
    @DisplayName("채팅방 생성 실패 - 존재하지 않는 공연장 ID")
    void createRoomFailed_NotFoundTheaterId() {

        // given
        TheaterIdRequest theaterIdRequest = new TheaterIdRequest();
        theaterIdRequest.setTheaterId(1L);
        given(theaterRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> chatRoomService.createRoom(theaterIdRequest));

        // then
        assertEquals(ErrorCode.NOT_FOUND_THEATER, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    @DisplayName("채팅방 삭제 성공")
    void deleteRoomSuccess() {

        // given
        Long roomId = 1L;
        ChatRoom chatRoom = new ChatRoom();
        given(chatRoomRepository.findById(anyLong())).willReturn(Optional.of(chatRoom));

        // when
        chatRoomService.deleteRoom(roomId);

        // then
        verify(chatRoomRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("채팅방 삭제 실패 - 존재하지 않는 채팅방 ID")
    void deleteRoomFailed_NotFoundChatRoomId() {

        // given
        Long roomId = 1L;
        given(chatRoomRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> chatRoomService.deleteRoom(roomId));

        // then
        assertEquals(ErrorCode.NOT_FOUND_CHATROOM, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

}