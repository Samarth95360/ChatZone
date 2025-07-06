package com.Chat_App.Chat_Service.DTO.Response;

import com.Chat_App.Chat_Service.Model.LastMessage;
import com.Chat_App.Chat_Service.Model.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserConversationData {

    private String chatRoomId;

    private LastMessage lastMessage;

    private String roomName;

    private RoomType roomType;

    private String status;

    private List<UUID> conversationUsersId;

}
