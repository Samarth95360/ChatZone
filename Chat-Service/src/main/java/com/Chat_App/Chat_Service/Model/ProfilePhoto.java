package com.Chat_App.Chat_Service.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "profile_photo")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProfilePhoto {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID userId;

    private String photoName;

    private String type;

    @Builder.Default
    private Instant creationTimeStamp = Instant.now();

    private String locationUrl;

}
