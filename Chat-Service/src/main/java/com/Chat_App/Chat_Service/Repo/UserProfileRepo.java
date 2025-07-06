package com.Chat_App.Chat_Service.Repo;

import com.Chat_App.Chat_Service.Model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserProfileRepo {

    private final MongoTemplate template;

    @Autowired
    public UserProfileRepo(MongoTemplate template) {
        this.template = template;
    }

    public UserProfile returnUserProfile(UUID userId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));
        return template.findOne(query, UserProfile.class);
    }

    public UserProfile saveProfile(UserProfile profile) {
        Query query = new Query(Criteria.where("_id").is(profile.getUserId()));
        Update update = new Update()
                .set("userName", profile.getUserName())
                .set("email", profile.getEmail())
                .set("chatRoomMetaData", profile.getChatRoomMetaData())
                .set("deletedRoomId", profile.getDeletedRoomId())
                .set("creationTimeStamp", profile.getCreationTimeStamp())
                .set("status",profile.getStatus())
                .set("phoneNo",profile.getPhoneNo());


        template.upsert(query, update, UserProfile.class);
        return profile;
    }


    public List<UserProfile> returnAllUsers(){
        return template.findAll(UserProfile.class);
    }

    public List<UserProfile> getUserProfileUsingGivenIds(List<UUID> userIds){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(userIds));

        return template.find(query,UserProfile.class);
    }

}
