package com.Chat_App.Auth_Service.Service.ResendOtp;

import com.Chat_App.Auth_Service.Models.User;
import com.Chat_App.Auth_Service.Repo.UserRepo;
import com.Chat_App.Auth_Service.Utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResendOtpService {

    private final UserRepo userRepo;
    private final TokenUtils tokenUtils;

    @Autowired
    public ResendOtpService(UserRepo userRepo, TokenUtils tokenUtils) {
        this.userRepo = userRepo;
        this.tokenUtils = tokenUtils;
    }

    public ResponseEntity<String> resendOtp(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UUID userId = UUID.fromString(authentication.getName());

        User user = userRepo.getReferenceById(userId);

        boolean token = tokenUtils.createToken(user.getEmail());
        if(token){
            return new ResponseEntity<>("Token Resend Success", HttpStatus.OK);
        }
        return new ResponseEntity<>("Can't resend the token",HttpStatus.BAD_REQUEST);
    }

}
