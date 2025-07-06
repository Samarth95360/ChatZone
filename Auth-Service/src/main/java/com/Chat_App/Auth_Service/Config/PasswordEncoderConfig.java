package com.Chat_App.Auth_Service.Config;

import com.Chat_App.Auth_Service.Components.CustomLoginAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
public class PasswordEncoderConfig {

    private final CustomLoginAuthProvider loginAuthProvider;

    @Autowired
    public PasswordEncoderConfig(CustomLoginAuthProvider loginAuthProvider) {
        this.loginAuthProvider = loginAuthProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(List.of(loginAuthProvider));
    }

}
