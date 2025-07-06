package com.Chat_App.Auth_Service.Components;

import com.Chat_App.Auth_Service.Service.Security.UserService;
import com.Chat_App.Auth_Service.Service.Security.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginAuthProvider implements AuthenticationProvider {

    private final UserServiceImpl userServiceImpl;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public CustomLoginAuthProvider(UserServiceImpl userServiceImpl, BCryptPasswordEncoder passwordEncoder){
        this.userServiceImpl = userServiceImpl;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("In custom login auth provider");
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserService userService = (UserService) userServiceImpl.loadUserByUsername(email);
        System.out.println(userService.getUsername());

        if(!userService.isAccountNonLocked()){
            throw new AccountExpiredException("Account Locked because of multiple failed login attempt");
        }

        if(!passwordEncoder.matches(password, userService.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userService,null,userService.getOtpAuthorities());

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
