package com.Chat_App.Auth_Service.Service.Registration;

import com.Chat_App.Auth_Service.DTO.request.RegisterRequest;
import com.Chat_App.Auth_Service.DTO.response.RegisterResponse;
import com.Chat_App.Auth_Service.Models.Role;
import com.Chat_App.Auth_Service.Models.User;
import com.Chat_App.Auth_Service.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserRegistrationService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserRegistrationService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<RegisterResponse> registerUser(RegisterRequest user){
        User previousUser = userRepo.findByEmail(user.getEmail());

        if(previousUser != null){
            RegisterResponse response = new RegisterResponse();
            response.setRegistered(false);
            response.setMessage("User Already Registered with this Email");
            response.setDateTime(LocalDateTime.now());
            response.setStatusCode(HttpStatus.CONFLICT);
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }

        User user1 = new User();
        user1.setEmail(user.getEmail());
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        user1.setFullName(user.getFullName());
        user1.setRole(Role.valueOf("ROLE_"+user.getRole()));

        userRepo.save(user1);

        RegisterResponse response = new RegisterResponse();
        response.setRegistered(true);
        response.setMessage("User Registration Success");
        response.setDateTime(LocalDateTime.now());
        response.setStatusCode(HttpStatus.OK);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

}
