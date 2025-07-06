package com.Chat_App.Chat_Service.Controller;

import com.Chat_App.Chat_Service.DTO.Request.GroupMemberProfilePayload;
import com.Chat_App.Chat_Service.DTO.Request.MessageRequestPayload;
import com.Chat_App.Chat_Service.DTO.Request.UpdateGroupInfo;
import com.Chat_App.Chat_Service.DTO.Response.Message;
import com.Chat_App.Chat_Service.DTO.Response.UserConversationData;
import com.Chat_App.Chat_Service.DTO.Response.UserList;
import com.Chat_App.Chat_Service.Service.Chat.ChatService;
import com.netflix.discovery.converters.Auto;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/chat")
@PreAuthorize("hasAnyRole('USER')")
public class ChatRestController {

    private final ChatService chatService;

    @Autowired
    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/messages")
    public ResponseEntity<List<Message>> returnUserMessage(@RequestBody MessageRequestPayload requestPayload){
        return chatService.getUserMessages(requestPayload);
    }

    @PostMapping("/group-member-profiles")
    public ResponseEntity<List<UserList>> fetchUserProfilesInGroup(@RequestBody GroupMemberProfilePayload payload) {
        if (payload.getChatRoomId() == null || payload.getChatRoomId().isBlank()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        return chatService.fetchUserProfilesInGroup(payload.getChatRoomId());
    }

    @PostMapping("/update-room-info")
    public ResponseEntity<UserConversationData> updateUserGroupInfo(@RequestBody UpdateGroupInfo groupInfo){
        return chatService.updateGroupInfo(groupInfo);
    }


}
