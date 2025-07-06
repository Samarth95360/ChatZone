package com.Chat_App.Auth_Service.Service.Otp;

import com.Chat_App.Auth_Service.DTO.request.TokenDTO;
import com.Chat_App.Auth_Service.DTO.response.LoginResponse;
import com.Chat_App.Auth_Service.Models.Role;
import com.Chat_App.Auth_Service.Models.User;
import com.Chat_App.Auth_Service.Repo.TokenRepo;
import com.Chat_App.Auth_Service.Repo.UserRepo;
import com.Chat_App.Auth_Service.Service.Jwt.JwtProvider;
import com.Chat_App.Auth_Service.Service.Security.UserService;
import com.Chat_App.Auth_Service.Utils.TokenUtils;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class OtpVerificationService {

    private final UserRepo userRepo;
    private final TokenUtils tokenUtils;
    private final JwtProvider jwtProvider;

    @Autowired
    public OtpVerificationService(UserRepo userRepo, TokenUtils tokenUtils, JwtProvider jwtProvider) {
        this.userRepo = userRepo;
        this.tokenUtils = tokenUtils;
        this.jwtProvider = jwtProvider;
    }

    public ResponseEntity<LoginResponse> verifyOtp(String otp){

        TokenDTO tokenDTO = tokenUtils.tokenVerification(otp);

        if(tokenDTO == null || !tokenDTO.isValid()){
            return new ResponseEntity<>(new LoginResponse("Invalid OTP" , null, LocalDateTime.now(),false), HttpStatus.NOT_ACCEPTABLE);
        }

        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());

        User user = userRepo.getReferenceById(userId);

        UserService userService = new UserService(user);

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean hasRoleToken = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(Role.ROLE_TOKEN.toString()));

        if (!hasRoleToken) {
            authorities = userService.getAuthorities();
        } else{
            authorities = List.of(new SimpleGrantedAuthority(Role.ROLE_PASSWORD.toString()));
        }

        String jwt = jwtProvider.jwtTokenGenerator(new UsernamePasswordAuthenticationToken(userService,null,authorities));

        if(hasRoleToken) {
            return new ResponseEntity<>(new LoginResponse("Valid Otp", jwt, LocalDateTime.now(), true), HttpStatus.OK);
        }
        return new ResponseEntity<>(new LoginResponse(String.valueOf(userId),jwt,LocalDateTime.now(),true),HttpStatus.OK);

    }

}
