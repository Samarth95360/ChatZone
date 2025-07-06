package com.Chat_App.Chat_Service.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CallInitiateRequest {

    private String chatRoomId;
    private UUID senderId;
    private List<UUID> receiverUsersId;
    private UUID receiverId;
    private String receiverName;
    private String callType;
    private Instant startingTime;
    private Instant expiryTime;
    private String callHealthStatus;


}
