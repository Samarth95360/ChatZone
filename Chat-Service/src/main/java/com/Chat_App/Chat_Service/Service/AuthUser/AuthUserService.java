package com.Chat_App.Chat_Service.Service.AuthUser;

import com.Chat_App.Chat_Service.DTO.Request.AddMemberToGroup;
import com.Chat_App.Chat_Service.DTO.Request.PrivateChatRoomInitiatePayload;
import com.Chat_App.Chat_Service.DTO.Response.BasicUserProfile;
import com.Chat_App.Chat_Service.DTO.Response.UserConversationData;
import com.Chat_App.Chat_Service.DTO.Response.UserList;
import com.Chat_App.Chat_Service.DTO.Response.UserProfileResponse;
import com.Chat_App.Chat_Service.Model.ChatRoom;
import com.Chat_App.Chat_Service.Model.ChatRoomMetaData;
import com.Chat_App.Chat_Service.Model.RoomType;
import com.Chat_App.Chat_Service.Model.UserProfile;
import com.Chat_App.Chat_Service.Repo.ChatRoomRepo;
import com.Chat_App.Chat_Service.Repo.UserProfileRepo;
import com.Chat_App.Chat_Service.Service.OpenFeign.AuthServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthUserService {

    private final AuthServiceClient serviceClient;
    private final UserProfileRepo userProfileRepo;
    private final ChatRoomRepo chatRoomRepo;

    @Autowired
    public AuthUserService(AuthServiceClient serviceClient, UserProfileRepo userProfileRepo, ChatRoomRepo chatRoomRepo) {
        this.serviceClient = serviceClient;
        this.userProfileRepo = userProfileRepo;
        this.chatRoomRepo = chatRoomRepo;
    }

    public ResponseEntity<List<UserList>> getListOfAllTheRegisteredUsers(){
        System.out.println("in auth user service");
        List<UserProfile> profileList = userProfileRepo.returnAllUsers();
        List<UserList> list = profileList.stream().map(data ->
                new UserList(data.getUserId(), data.getUserName(), data.getStatus())
            ).toList();

        return ResponseEntity.ok(list);
    }

    public ResponseEntity<List<UserConversationData>> returnUserConversationGroupData() {

        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        UserProfile userProfile = checkForUserProfile(userId);

        if (userProfile == null || userProfile.getChatRoomMetaData() == null) {
            return ResponseEntity.badRequest().build();
        }

        List<UserConversationData> finalData = new ArrayList<>();

        for (ChatRoomMetaData data : userProfile.getChatRoomMetaData()) {

            ChatRoom room = chatRoomRepo.getChatRoomUsingId(data.getChatRoomId());
            String groupName;
            String status;

            if (data.getRoomType() == RoomType.Group && data.getRoomSize() > 2) {
                groupName = room.getRoomName();
                status = room.getGroupStatus();
            } else if (data.getRoomType() == RoomType.Single && data.getRoomSize() == 1) {
                groupName = room.getRoomName();
                status = userProfile.getStatus();
            } else {
                // For 1-1 Chat, derive other participant's name
                Iterator<UUID> it = room.getConversationUserId().iterator();
                UUID first = it.next();
                UUID second = it.hasNext() ? it.next() : null;

                UUID otherUserId = Objects.equals(first, userId) ? second : first;
                UserProfile otherUser = checkForUserProfile(otherUserId);
                groupName = otherUser != null ? otherUser.getUserName() : "Unknown User";
                status = otherUser != null ? otherUser.getStatus() : "";
            }

            Set<UUID> usersIds = room.getConversationUserId();

            finalData.add(new UserConversationData(
                    room.getId(),
                    room.getLastMessage(),
                    groupName,
                    room.getRoomType(),
                    status,
                    usersIds.stream().toList()
            ));
        }

        return ResponseEntity.ok(finalData);
    }


    public ResponseEntity<UserConversationData> initiatePrivateConversation(PrivateChatRoomInitiatePayload payload) {

        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if(userId == null || payload.getReceiverId() == null){
            return ResponseEntity.badRequest().build();
        }

        System.out.println("UUID is :- "+userId);
        System.out.println("Payload is :- "+payload.toString());

        UserProfile sender = checkForUserProfile(userId);
        UserProfile receiver = checkForUserProfile(payload.getReceiverId());

        if (sender == null || receiver == null) {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Sender profile :- "+sender.toString());
        System.out.println("receiver profile :- "+receiver.toString());

        if(userId.equals(payload.getReceiverId())){
            for(ChatRoomMetaData data : sender.getChatRoomMetaData()){
                if(data.getRoomSize() == 1 && data.getRoomType().equals(RoomType.Single)){
                    ChatRoom room = chatRoomRepo.getChatRoomUsingId(data.getChatRoomId());

                    Set<UUID> usersIds = room.getConversationUserId();

                    return ResponseEntity.ok(new UserConversationData(
                            room.getId(),
                            room.getLastMessage(),
                            sender.getUserName(),
                            room.getRoomType(),
                            sender.getStatus(),
                            usersIds.stream().toList()
                    ));
                }
            }
        }

        // Sorted Set ensures consistent user ID order
        Set<UUID> conversationIds = new TreeSet<>(Arrays.asList(userId, payload.getReceiverId()));

        // Step 1: Check if conversation already exists
        if (sender.getChatRoomMetaData() != null) {
            for (ChatRoomMetaData meta : sender.getChatRoomMetaData()) {
                if (meta.getRoomType() == RoomType.Double && meta.getRoomSize() == 2) {
                    ChatRoom room = chatRoomRepo.getChatRoomUsingId(meta.getChatRoomId());
                    if (room != null && room.getConversationUserId().equals(conversationIds)) {

                        Set<UUID> usersIds = room.getConversationUserId();

                        // Already exists — return the conversation data
                        return ResponseEntity.ok(new UserConversationData(
                                room.getId(),
                                room.getLastMessage(),
                                receiver.getUserName(),
                                room.getRoomType(),
                                receiver.getStatus(),
                                usersIds.stream().toList()
                        ));
                    }
                }
            }
        }

        // Step 2: Create a new conversation if not found
        ChatRoom newRoom = ChatRoom.builder()
                .conversationUserId(conversationIds)
                .numberOfParticipants(2)
                .roomType(RoomType.Double)
                .build();

        newRoom = chatRoomRepo.saveChatRoom(newRoom);

        ChatRoomMetaData metaData = ChatRoomMetaData.builder()
                .chatRoomId(newRoom.getId())
                .roomSize(2)
                .roomType(RoomType.Double)
                .build();

        sender.addChatRoomId(metaData);
        receiver.addChatRoomId(metaData);

        userProfileRepo.saveProfile(sender);
        userProfileRepo.saveProfile(receiver);

        Set<UUID> usersIds = newRoom.getConversationUserId();

        // Return new conversation data
        return ResponseEntity.ok(new UserConversationData(
                newRoom.getId(),
                newRoom.getLastMessage(),
                receiver.getUserName(),
                RoomType.Double,
                receiver.getStatus(),
                usersIds.stream().toList()
        ));
    }

    public ResponseEntity<UserConversationData> addMembersToGroup(AddMemberToGroup request) {

        ChatRoom room = chatRoomRepo.getChatRoomUsingId(request.getChatRoomId());
        if (room == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        Set<UUID> existingMembers = room.getConversationUserId();
        Set<UUID> allUserIds = new HashSet<>(existingMembers);
        allUserIds.addAll(request.getUserIdsToAdd());

        if (allUserIds.isEmpty()) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        List<UserProfile> allProfiles = new ArrayList<>();
        for (UUID userId : allUserIds) {
            UserProfile profile = userProfileRepo.returnUserProfile(userId);
            if (profile != null) allProfiles.add(profile);
        }

        if (allProfiles.isEmpty()) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        int updatedSize = allUserIds.size();

        for (UserProfile profile : allProfiles) {
            boolean alreadyPresent = false;
            for (ChatRoomMetaData data : profile.getChatRoomMetaData()) {
                if (data.getChatRoomId().equals(room.getId())) {
                    data.setRoomType(RoomType.Group);
                    data.setRoomSize(updatedSize);
                    alreadyPresent = true;
                    break;
                }
            }
            if (!alreadyPresent) {
                profile.addChatRoomId(new ChatRoomMetaData(room.getId(), updatedSize, RoomType.Group));
            }
            userProfileRepo.saveProfile(profile);
        }

        room.setConversationUserId(allUserIds);
        room.setNumberOfParticipants(updatedSize);
        room.setRoomType(RoomType.Group);

        if (room.getRoomName() == null || room.getRoomName().isBlank()) {
            room.setRoomName(generateDefaultGroupName(allUserIds));
        }

        room = chatRoomRepo.saveChatRoom(room);

        Set<UUID> usersIds = room.getConversationUserId();

        UserConversationData responseData = UserConversationData.builder()
                .chatRoomId(room.getId())
                .roomType(room.getRoomType())
                .roomName(room.getRoomName())
                .lastMessage(room.getLastMessage())
                .status(room.getGroupStatus())
                .conversationUsersId(usersIds.stream().toList())
                .build();

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }


    public ResponseEntity<UserProfileResponse> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof String)) {
            return ResponseEntity.badRequest().build();
        }

        UUID userId;
        try {
            userId = UUID.fromString(authentication.getPrincipal().toString());
        } catch (IllegalArgumentException ex) {
            throw new UsernameNotFoundException("Invalid user ID in authentication principal");
        }

        UserProfile profile = Optional.ofNullable(userProfileRepo.returnUserProfile(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User profile not found"));

        UserProfileResponse response = UserProfileResponse.builder()
                .userId(profile.getUserId())
                .userName(profile.getUserName())
                .status(profile.getStatus())
                .phoneNo(profile.getPhoneNo())
                .creationTimeStamp(profile.getCreationTimeStamp())
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<UserConversationData> updateUserProfile(UserProfileResponse response) {
        UUID userId = response.getUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        UserProfile profile = userProfileRepo.returnUserProfile(userId);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        // Update fields
        profile.setStatus(response.getStatus());
        profile.setPhoneNo(response.getPhoneNo());
        profile = userProfileRepo.saveProfile(profile);

        // Find associated single chat room
        String conversationId = profile.getChatRoomMetaData()
                .stream()
                .filter(meta -> meta.getRoomSize() == 1 && meta.getRoomType() == RoomType.Single)
                .map(ChatRoomMetaData::getChatRoomId)
                .findFirst()
                .orElse(null);

        if (conversationId == null) {
            return ResponseEntity.badRequest().build();
        }

        ChatRoom room = chatRoomRepo.getChatRoomUsingId(conversationId);
        if (room == null) {
            return ResponseEntity.badRequest().build();
        }

        // Sync room details
        room.setGroupStatus(profile.getStatus());
        room.setRoomName(profile.getUserName());
        room = chatRoomRepo.saveChatRoom(room);

        Set<UUID> usersIds = room.getConversationUserId();

        UserConversationData conversationData = UserConversationData.builder()
                .chatRoomId(room.getId())
                .roomName(room.getRoomName())
                .status(room.getGroupStatus())
                .lastMessage(room.getLastMessage())
                .roomType(room.getRoomType())
                .conversationUsersId(usersIds.stream().toList())
                .build();

        return ResponseEntity.ok(conversationData);
    }


    private String generateDefaultGroupName(Set<UUID> userIds) {
        return userIds.stream()
                .limit(3) // Show only 3 names max
                .map(userProfileRepo::returnUserProfile)
                .filter(Objects::nonNull)
                .map(UserProfile::getUserName)
                .reduce((name1, name2) -> name1 + ", " + name2)
                .orElse("New Group");
    }

    public void createUserProfiles(){
        ResponseEntity<List<UserList>> userLists = serviceClient.getListOfAllUsers();
        List<UserList> list = userLists.getBody();
        for(UserList data : list){
            checkForUserProfile(data.getId());
        }
    }



    private UserProfile checkForUserProfile(UUID userId) {

        if(userId == null){
            return null;
        }

        UserProfile userProfile = userProfileRepo.returnUserProfile(userId);

        if (userProfile != null) {
            return userProfile;
        }

        // Fetch basic user profile from external service
        ResponseEntity<BasicUserProfile> response = serviceClient.getUserProfileData(userId);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return null;
        }

        BasicUserProfile basicProfile = response.getBody();

        ChatRoom chatRoom = chatRoomRepo.saveChatRoom(ChatRoom.builder()
                .conversationUserId(Collections.singleton(basicProfile.getId()))
                .numberOfParticipants(1)
                .roomName(Optional.ofNullable(basicProfile.getFullName()).orElse("Anonymous User"))
                .roomType(RoomType.Single)
                .build()
        );

        userProfile = UserProfile.builder()
                .userId(basicProfile.getId())
                .userName(basicProfile.getFullName())
                .email(basicProfile.getEmail())
                .build();

        userProfile.addChatRoomId(ChatRoomMetaData.builder()
                .chatRoomId(chatRoom.getId())
                .roomType(chatRoom.getRoomType())
                .roomSize(chatRoom.getNumberOfParticipants())
                .build()
        );

        return userProfileRepo.saveProfile(userProfile);
    }



}
