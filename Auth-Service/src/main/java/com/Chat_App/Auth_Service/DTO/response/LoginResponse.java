package com.Chat_App.Auth_Service.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {

    private String message;
    private String jwt;
    private LocalDateTime dateTime;
    private boolean isJwtTokenAllocated;

}
