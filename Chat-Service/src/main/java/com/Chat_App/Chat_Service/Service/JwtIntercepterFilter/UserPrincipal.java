package com.Chat_App.Chat_Service.Service.JwtIntercepterFilter;

import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;

public class UserPrincipal implements Principal {

    private final String userId;

    @Autowired
    public UserPrincipal(String userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return userId;
    }
}
