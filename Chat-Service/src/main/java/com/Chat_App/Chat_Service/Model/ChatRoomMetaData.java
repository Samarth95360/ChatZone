package com.Chat_App.Chat_Service.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatRoomMetaData {

    private String chatRoomId;
    private int roomSize;
    private RoomType roomType;

}
