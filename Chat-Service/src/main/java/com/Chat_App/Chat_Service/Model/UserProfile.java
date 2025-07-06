package com.Chat_App.Chat_Service.Model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "user_profile")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserProfile {

    @Id
    @Indexed(unique = true)
    private UUID userId;

    @Indexed(unique = true) // Unique usernames are best enforced this way in MongoDB
    private String userName;

    @Length(min = 10, max = 10)
    private String phoneNo;

    private String status;

    @Email(regexp = "^[a-zA-Z0-9_%]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    @Builder.Default
    private Instant creationTimeStamp = Instant.now();

    @Builder.Default
    private List<ChatRoomMetaData> chatRoomMetaData = new ArrayList<>();

    @Builder.Default
    private List<String> deletedRoomId = new ArrayList<>();

    public void addChatRoomId(ChatRoomMetaData metaData) {
        if (chatRoomMetaData != null && !chatRoomMetaData.equals(metaData)) {
            chatRoomMetaData.add(metaData);
        }
    }

    public void addDeletedRoomId(String roomId) {
        if (roomId != null && !deletedRoomId.contains(roomId)) {
            deletedRoomId.add(roomId);
        }
    }

}
