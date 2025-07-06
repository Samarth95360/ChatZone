package com.Chat_App.Auth_Service.DTO.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

    private String fullName;

    @Email
    private String email;

    @NotBlank
    @NotEmpty
    private String password;

    @Enumerated(EnumType.STRING)
    private String role;

}
