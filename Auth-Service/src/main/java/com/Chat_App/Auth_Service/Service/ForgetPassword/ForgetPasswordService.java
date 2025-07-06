package com.Chat_App.Auth_Service.Service.ForgetPassword;

import com.Chat_App.Auth_Service.DTO.request.UpdatedPassword;
import com.Chat_App.Auth_Service.DTO.response.LoginResponse;
import com.Chat_App.Auth_Service.Models.Role;
import com.Chat_App.Auth_Service.Models.User;
import com.Chat_App.Auth_Service.Repo.UserRepo;
import com.Chat_App.Auth_Service.Service.Jwt.JwtProvider;
import com.Chat_App.Auth_Service.Utils.TokenUtils;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgetPasswordService {

    private final UserRepo userRepo;
    private final JwtProvider jwtProvider;
    private final TokenUtils tokenUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public ForgetPasswordService(UserRepo userRepo, JwtProvider jwtProvider, TokenUtils tokenUtils, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtProvider = jwtProvider;
        this.tokenUtils = tokenUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<LoginResponse> forgetPasswordEmail(String userEmail){
        User user = userRepo.findByEmail(userEmail);

        if(user == null){
            return new ResponseEntity<>(new LoginResponse("Not a valid user",null, LocalDateTime.now(),false),HttpStatus.NOT_ACCEPTABLE);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail,null, List.of(new SimpleGrantedAuthority(Role.ROLE_TOKEN.toString())));
        String jwt = jwtProvider.jwtTokenGenerator(authentication);
        boolean isToken = tokenUtils.createToken(userEmail);

        if(!isToken){
            return new ResponseEntity<>(new LoginResponse("Something Went wrong can't create a token",null, LocalDateTime.now(),false),HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(new LoginResponse(
                "We have send you a Otp at your registered email ",
                jwt,
                LocalDateTime.now(),
                true
        ),HttpStatus.OK);

    }

    public ResponseEntity<String> validatePasswordAndChange(UpdatedPassword request){
        if(!request.getPassword().equals(request.getConfirmPassword())){
            return new ResponseEntity<>("Invalid Password . Please provide a valid password in both the fields",HttpStatus.BAD_REQUEST);
        }
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Optional<User> user = userRepo.findById(userId);

        if (user.isPresent()){
            if(passwordEncoder.matches(request.getPassword(), user.get().getPassword())){
                return new ResponseEntity<>("Password can't be same as before . Please try something new.",HttpStatus.BAD_REQUEST);
            }
            user.get().setPassword(passwordEncoder.encode(request.getPassword()));
            userRepo.save(user.get());
            return new ResponseEntity<>("Password Change Success" , HttpStatus.OK);
        }
        return new ResponseEntity<>("SomeThing went wrong . Try Again" , HttpStatus.BAD_GATEWAY);
    }

}
