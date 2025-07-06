package com.Chat_App.Chat_Service.DTO.Response;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasicUserProfile {

    private UUID id;

    private String fullName;

    @Email
    private String email;

}
