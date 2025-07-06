package com.Chat_App.Chat_Service.DTO.Request;

import com.Chat_App.Chat_Service.Model.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PrivateChatRoomInitiatePayload {

    private UUID receiverId;

    private RoomType roomType;

}
