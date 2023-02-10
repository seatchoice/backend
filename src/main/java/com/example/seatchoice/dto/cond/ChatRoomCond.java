package com.example.seatchoice.dto.cond;

import com.example.seatchoice.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomCond {
    private Long roomId;

    public static ChatRoomCond from(ChatRoom chatRoom) {
        return ChatRoomCond.builder()
            .roomId(chatRoom.getId())
            .build();
    }
}
