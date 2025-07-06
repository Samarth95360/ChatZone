package com.Chat_App.Chat_Service.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserList {

    private UUID id;
    private String userName;
    private String userStatus;

}
