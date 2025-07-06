package com.Chat_App.Auth_Service.Service.Login;

import com.Chat_App.Auth_Service.DTO.request.LoginRequest;
import com.Chat_App.Auth_Service.DTO.response.LoginResponse;
import com.Chat_App.Auth_Service.Service.Jwt.JwtProvider;
import com.Chat_App.Auth_Service.Utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserLoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final TokenUtils tokenUtils;

    @Autowired
    public UserLoginService(AuthenticationManager authenticationManager, JwtProvider jwtProvider, TokenUtils tokenUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.tokenUtils = tokenUtils;
    }

    public ResponseEntity<LoginResponse> loginUser(LoginRequest loginRequest){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword())
        );
        System.out.println("in login service");
        String jwt = jwtProvider.jwtTokenGenerator(authentication);
        System.out.println(jwt);
        boolean token = tokenUtils.createToken(loginRequest.getEmail());
        LoginResponse responseDTO = new LoginResponse();
        responseDTO.setDateTime(LocalDateTime.now());
        if(jwt != null && token) {
            responseDTO.setJwt(jwt);
            responseDTO.setMessage("Jwt created Success");
            responseDTO.setJwtTokenAllocated(true);
        }else{
            responseDTO.setJwt(null);
            responseDTO.setMessage("Jwt Creation Fail");
            responseDTO.setJwtTokenAllocated(false);
        }
        return jwt == null ? new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST) : new ResponseEntity<>(responseDTO,HttpStatus.CREATED);


    }

}
