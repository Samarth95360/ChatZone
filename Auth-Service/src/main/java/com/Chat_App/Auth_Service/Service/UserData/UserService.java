package com.Chat_App.Auth_Service.Service.UserData;

import com.Chat_App.Auth_Service.DTO.response.BasicUserProfile;
import com.Chat_App.Auth_Service.DTO.response.Users;
import com.Chat_App.Auth_Service.Models.User;
import com.Chat_App.Auth_Service.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public ResponseEntity<List<Users>> listOfAllUsers(){
        List<Users> usersList = userRepo.findAll()
                .stream()
                .map(user -> new Users(user.getId(), user.getFullName()))
                .toList();
        return new ResponseEntity<>(usersList, HttpStatus.OK);

    }

    public ResponseEntity<BasicUserProfile> getUserProfileData(UUID userId) {

        System.out.println("UUID of the user is :- " + userId);

        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            BasicUserProfile profile = new BasicUserProfile(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail()
            );
            System.out.println("In auth service for chat service");
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
