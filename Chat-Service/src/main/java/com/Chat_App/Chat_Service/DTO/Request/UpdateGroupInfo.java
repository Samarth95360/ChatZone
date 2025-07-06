package com.Chat_App.Chat_Service.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateGroupInfo {

    private String chatRoomId;
    private String roomName;
    private String roomStatus;

}
