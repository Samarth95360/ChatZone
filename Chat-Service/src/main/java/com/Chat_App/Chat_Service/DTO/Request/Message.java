package com.Chat_App.Chat_Service.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {

    private UUID senderId;
    private String chatRoomId;
    private String text;
    private Instant dateTime;

    @Override
    public String toString() {
        return "Message{" +
                "senderId=" + senderId +
                ", receiverId=" + chatRoomId +
                ", text='" + text + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
