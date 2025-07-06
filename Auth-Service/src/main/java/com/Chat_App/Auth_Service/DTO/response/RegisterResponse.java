package com.Chat_App.Auth_Service.DTO.response;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterResponse {

    private String message;
    private HttpStatus statusCode;
    private boolean isRegistered;
    private LocalDateTime dateTime;

}
