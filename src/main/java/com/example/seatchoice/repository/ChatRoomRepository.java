package com.example.seatchoice.repository;

import com.example.seatchoice.entity.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 공연장 fk로 방 검색
    Optional<ChatRoom> findByTheaterId(Long theaterId);
}
