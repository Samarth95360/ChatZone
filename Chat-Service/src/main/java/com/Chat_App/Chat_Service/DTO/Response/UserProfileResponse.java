package com.Chat_App.Chat_Service.DTO.Response;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserProfileResponse {

    private UUID userId;

    private String userName;

    @Length(min = 10, max = 10)
    private String phoneNo;

    private String status;

    private Instant creationTimeStamp;

}
