package com.Chat_App.Chat_Service.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Document(collection = "message")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {

    @Id
    private String id;

    @Indexed
    private String chatRoomId;

    private UUID sendBy;

    private String text;

    private boolean deleted = false;

    private boolean edited = false;

    @Builder.Default
    private List<MessageEditHistory> editHistory = new ArrayList<>();

    @Indexed
    @Builder.Default
    private Instant dateTime = Instant.now();

    @Builder.Default
    private Set<UUID> deletedByUsers = new TreeSet<>();

    public void setEditHistory(MessageEditHistory messageEditHistory){
        if(editHistory == null){
            editHistory = new ArrayList<>();
        }
        editHistory.add(messageEditHistory);
    }

    public void setDeletedByUsers(UUID userId){
        if(deletedByUsers == null){
            deletedByUsers = new TreeSet<>();
        }
        deletedByUsers.add(userId);
    }

}
