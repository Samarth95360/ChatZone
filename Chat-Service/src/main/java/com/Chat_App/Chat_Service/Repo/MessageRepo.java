package com.Chat_App.Chat_Service.Repo;

import com.Chat_App.Chat_Service.Model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Repository
public class MessageRepo {


    private final MongoTemplate mongoTemplate;

    @Autowired
    public MessageRepo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Message> fetchConversation(List<String> convIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(convIds));
        query.with(Sort.by(Sort.Direction.ASC, "dateTime")); // or DESC if preferred

        // Step 4: Fetch messages
        return mongoTemplate.find(query, Message.class);
    }

    public Message saveMessage(Message message){

        return mongoTemplate.save(message);

    }

    public Message findById(String id){
        return mongoTemplate.findById(id, Message.class);
    }


}
