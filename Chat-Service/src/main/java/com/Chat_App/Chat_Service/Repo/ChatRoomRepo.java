package com.Chat_App.Chat_Service.Repo;

import com.Chat_App.Chat_Service.Model.ChatRoom;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Repository
public class ChatRoomRepo {

    private final MongoTemplate template;

    public ChatRoomRepo(MongoTemplate template) {
        this.template = template;
    }

    public ChatRoom returnRoomForAUser(UUID user1, UUID user2){
        Query query = new Query();
        query.addCriteria(Criteria.where("senderId").is(user1).and("receiverId").is(user2)
        );

        return template.findOne(query, ChatRoom.class);
    }

    public ChatRoom saveChatRoom(ChatRoom chat){
        return template.save(chat);
    }

    public List<ChatRoom> getChatRoomsByIds(List<String> chatRoomIds) {
        if (chatRoomIds == null || chatRoomIds.isEmpty()) {
            return Collections.emptyList(); // No rooms to fetch
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(chatRoomIds));

        return template.find(query, ChatRoom.class);
    }

    public ChatRoom getChatRoomUsingId(String roomId){
        if(roomId == null){
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(roomId));

        return template.findOne(query, ChatRoom.class);
    }

}
