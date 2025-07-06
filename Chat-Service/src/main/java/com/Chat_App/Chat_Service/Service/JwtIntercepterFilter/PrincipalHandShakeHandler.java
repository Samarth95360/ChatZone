package com.Chat_App.Chat_Service.Service.JwtIntercepterFilter;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

public class PrincipalHandShakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String userId = (String) attributes.get("userId"); // Use "userId" instead of "username"
        if (userId != null) {
            return new UserPrincipal(userId);
        }
        return null;
    }

}
