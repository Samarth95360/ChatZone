package com.Chat_App.Chat_Service.Service.Chat;

import com.Chat_App.Chat_Service.DTO.Request.MessageRequestPayload;
import com.Chat_App.Chat_Service.DTO.Request.UpdateGroupInfo;
import com.Chat_App.Chat_Service.DTO.Response.UserConversationData;
import com.Chat_App.Chat_Service.DTO.Response.UserList;
import com.Chat_App.Chat_Service.Model.*;
//import com.Chat_App.Chat_Service.Model.PrivateChatRoom;
import com.Chat_App.Chat_Service.Repo.MessageRepo;
import com.Chat_App.Chat_Service.Repo.ChatRoomRepo;
import com.Chat_App.Chat_Service.Repo.UserProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.*;

@Service
public class ChatService {

    private final MessageRepo messageRepo;
    private final ChatRoomRepo chatRoomRepo;
    private final UserProfileRepo userProfileRepo;

    @Autowired
    public ChatService(MessageRepo messageRepo, ChatRoomRepo chatRoomRepo, UserProfileRepo userProfileRepo) {
        this.messageRepo = messageRepo;
        this.chatRoomRepo = chatRoomRepo;
        this.userProfileRepo = userProfileRepo;
    }

    public ResponseEntity<List<com.Chat_App.Chat_Service.DTO.Response.Message>> getUserMessages(MessageRequestPayload users){

        ChatRoom room = chatRoomRepo.getChatRoomUsingId(users.getConvId());

        if(room == null || room.getConversationUserId().isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());

        }

        List<Message> messages = messageRepo.fetchConversation(room.getRecentMessagesIds());
        List<com.Chat_App.Chat_Service.DTO.Response.Message> response = messages.stream().map(msg -> com.Chat_App.Chat_Service.DTO.Response.Message.builder()
                        .id(msg.getId())
                        .sendBy(msg.getSendBy())
                        .text(msg.getText())
                        .deleted(msg.isDeleted())
                        .edited(msg.isEdited())
                        .dateTime(msg.getDateTime())
                .build()
        ).toList();

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    public Message editMessage(com.Chat_App.Chat_Service.DTO.Response.Message messageDto, Principal principal) {

        // Step 1: Get existing message
        Message existing = messageRepo.findById(messageDto.getId());

        if (existing == null) {
            throw new NoSuchElementException("Message not found with id: " + messageDto.getId());
        }

        // Step 2: Validate sender (more secure via SecurityContext)
        UUID currentUserId = UUID.fromString(principal.getName());

        if (!messageDto.getSendBy().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to edit this message");
        }

        // Step 3: Skip if text is unchanged
        if (existing.getText().equals(messageDto.getText())) {
            return existing;
        }

        // Step 4: Add to edit history and update
        MessageEditHistory history = MessageEditHistory.builder()
                .oldText(existing.getText())
                .timeStamp(Instant.now())
                .build();
        existing.setEdited(true);
        existing.setEditHistory(history);
        existing.setText(messageDto.getText());

        messageRepo.saveMessage(existing);

        // Step 5: Return updated DTO
        return existing;
    }

    public Message deleteMessage(com.Chat_App.Chat_Service.DTO.Response.Message messageDto , Principal principal){

        Message userMessage = messageRepo.findById(messageDto.getId());

        if(userMessage == null){
            throw new NoSuchElementException("Message not found with id: " + messageDto.getId());
        }

        UUID userId = UUID.fromString(principal.getName());

        if(!messageDto.getSendBy().equals(userId)){
            throw new SecurityException("You are not authorized to delete this message");
        }

        userMessage.setDeleted(true);
        userMessage = messageRepo.saveMessage(userMessage);

        return userMessage;

    }

    @Transactional
    public com.Chat_App.Chat_Service.DTO.Response.Message saveMessage(com.Chat_App.Chat_Service.DTO.Request.Message message) {

        ChatRoom chatRoom = chatRoomRepo.getChatRoomUsingId(message.getChatRoomId());

        if(chatRoom == null){

            return null;

        }

        Message newMessage = Message.builder()
                .chatRoomId(message.getChatRoomId())
                .sendBy(message.getSenderId())
                .text(message.getText())
                .deleted(false)
                .edited(false)
                .build();

        newMessage = messageRepo.saveMessage(newMessage);

        chatRoom.setLastMessage(new LastMessage(newMessage.getId(), newMessage.getText(), Instant.now(),newMessage.getSendBy()));
        chatRoom.addLatestMessageId(newMessage.getId());

        chatRoomRepo.saveChatRoom(chatRoom);

        com.Chat_App.Chat_Service.DTO.Response.Message responseMessage = com.Chat_App.Chat_Service.DTO.Response.Message.builder()
                .id(newMessage.getId())
                .sendBy(newMessage.getSendBy())
                .text(newMessage.getText())
                .deleted(newMessage.isDeleted())
                .edited(newMessage.isEdited())
                .dateTime(newMessage.getDateTime())
                .build();

        return responseMessage;

    }

    public ResponseEntity<List<UserList>> fetchUserProfilesInGroup(String chatRoomId) {

        ChatRoom room = chatRoomRepo.getChatRoomUsingId(chatRoomId);

        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        // Return only if it's a group conversation
        if (!RoomType.Group.equals(room.getRoomType())) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Set<UUID> userIds = room.getConversationUserId();

        if (userIds == null || userIds.size() <= 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        if(!userIds.contains(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }

        List<UserProfile> profiles = userProfileRepo.getUserProfileUsingGivenIds(new ArrayList<>(userIds));

        List<UserList> response = profiles.stream()
                .map(profile -> new UserList(profile.getUserId(), profile.getUserName(), profile.getStatus()))
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<UserConversationData> updateGroupInfo(UpdateGroupInfo groupInfo) {
        if (groupInfo == null || groupInfo.getChatRoomId() == null) {
            return ResponseEntity.badRequest().build();
        }

        ChatRoom room = chatRoomRepo.getChatRoomUsingId(groupInfo.getChatRoomId());
        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!RoomType.Group.equals(room.getRoomType()) || room.getConversationUserId() == null || room.getConversationUserId().size() <= 2) {
            return ResponseEntity.badRequest().build();
        }

        UUID currentUserId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        if (!room.getConversationUserId().contains(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Update and save group details
        room.setRoomName(groupInfo.getRoomName());
        room.setGroupStatus(groupInfo.getRoomStatus());
        room = chatRoomRepo.saveChatRoom(room);

        Set<UUID> usersIds = room.getConversationUserId();

        // Prepare response DTO
        UserConversationData response = UserConversationData.builder()
                .chatRoomId(room.getId())
                .roomName(room.getRoomName())
                .roomType(room.getRoomType())
                .status(room.getGroupStatus())
                .lastMessage(room.getLastMessage())
                .conversationUsersId(usersIds.stream().toList())
                .build();

        return ResponseEntity.ok(response);
    }




}
