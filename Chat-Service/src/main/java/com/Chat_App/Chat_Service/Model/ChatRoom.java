package com.Chat_App.Chat_Service.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Document(collection = "chat_room")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatRoom {

    @Id
    private String id;

    @Builder.Default
    private Set<UUID> conversationUserId = new TreeSet<>();  // This field is going to store the uuid if all the user that have taken participation in chatting in sorted order

    private LastMessage lastMessage;

    @Builder.Default
    private Instant creationTimeStamp = Instant.now();

    private int numberOfParticipants;

    private String roomName;   //Group name

    private RoomType roomType; // "single", "double", or "group"

    private String groupStatus;

    @Builder.Default
    private Map<UUID, Boolean> isDeletedForUser = new HashMap<>();

    @Builder.Default
    private LinkedList<String> recentMessagesIds = new LinkedList<>();

    public void addLatestMessageId(String messageId) {

        if (recentMessagesIds.size() > 20) {
            recentMessagesIds.removeFirst();
        }
        recentMessagesIds.addLast(messageId);
    }


}
