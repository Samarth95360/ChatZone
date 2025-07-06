package com.Chat_App.Chat_Service.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {

    private String id;
    private UUID sendBy;
    private String text;
    private boolean deleted;
    private boolean edited;
    private Instant dateTime;

}
