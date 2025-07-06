package com.Chat_App.Chat_Service.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddMemberToGroup {

    private String chatRoomId;
    private List<UUID> userIdsToAdd;

}
