package com.Chat_App.Chat_Service.Controller;

import com.Chat_App.Chat_Service.DTO.Request.AddMemberToGroup;
import com.Chat_App.Chat_Service.DTO.Request.PrivateChatRoomInitiatePayload;
import com.Chat_App.Chat_Service.DTO.Response.UserConversationData;
import com.Chat_App.Chat_Service.DTO.Response.UserList;
import com.Chat_App.Chat_Service.DTO.Response.UserProfileResponse;
import com.Chat_App.Chat_Service.Service.AuthUser.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('USER')")
public class UserController {

    private final AuthUserService authUserService;

    @Autowired
    public UserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserList>> getListOfAllRegisteredUsers(){
        System.out.println("in user list controller");
        return authUserService.getListOfAllTheRegisteredUsers();
    }

    @GetMapping("/profile")
    public ResponseEntity<List<UserConversationData>> returnUserConversationGroupData(){
        System.out.println("in up controller");
        ResponseEntity<List<UserConversationData>> data = authUserService.returnUserConversationGroupData();
        System.out.println("Data is controller :- "+data+"----status code is :- "+data.getStatusCode());
        return data;
    }

    @PostMapping("/initiate-private-conv")
    public ResponseEntity<UserConversationData> initiatePrivateConversation(@RequestBody PrivateChatRoomInitiatePayload payload){
        ResponseEntity<UserConversationData> data = authUserService.initiatePrivateConversation(payload);
        System.out.println("Initiate private conv :- "+data);
        return data;
    }

    @PostMapping("/add-members")
    public ResponseEntity<UserConversationData> addMembersToGroup(@RequestBody AddMemberToGroup members){
        return authUserService.addMembersToGroup(members);
    }

    // dummy create all user profile in one go
    @GetMapping("/create-user-profile")
    public void createAllUserProfile(){
        authUserService.createUserProfiles();
    }

    @GetMapping("/user-profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(){
        return authUserService.getUserProfile();
    }

    @PostMapping("/update-user-profile")
    public ResponseEntity<UserConversationData> updateUserProfile(@RequestBody UserProfileResponse response){
        return authUserService.updateUserProfile(response);
    }

}
