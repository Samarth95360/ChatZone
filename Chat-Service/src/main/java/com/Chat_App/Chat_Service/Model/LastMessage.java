package com.Chat_App.Chat_Service.Model;

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
public class LastMessage {

    private String messageId;
    private String text;
    private Instant timeStamp;
    private UUID senderId;

}
