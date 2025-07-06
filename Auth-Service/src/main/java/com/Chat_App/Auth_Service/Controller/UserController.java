package com.Chat_App.Auth_Service.Controller;

import com.Chat_App.Auth_Service.DTO.response.BasicUserProfile;
import com.Chat_App.Auth_Service.DTO.response.Users;
import com.Chat_App.Auth_Service.Service.UserData.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('USER')")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Users>> getListOfAllUsers(){
        System.out.println("in user service auth");
        return userService.listOfAllUsers();
    }


    @GetMapping("/profile")
    public ResponseEntity<BasicUserProfile> getUserProfileData(@RequestParam UUID userId){
        System.out.println("in user service auth for profile");
        return userService.getUserProfileData(userId);
    }

}
