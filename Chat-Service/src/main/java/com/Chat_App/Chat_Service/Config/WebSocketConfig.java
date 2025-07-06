package com.Chat_App.Chat_Service.Config;

import com.Chat_App.Chat_Service.Service.JwtIntercepterFilter.JwtWebSocketInterceptor;
import com.Chat_App.Chat_Service.Service.JwtIntercepterFilter.PrincipalHandShakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins("http://localhost:3000","http://localhost:3001","http://localhost:5173","http://localhost:5174")
                .setHandshakeHandler(new PrincipalHandShakeHandler())
                .addInterceptors(new JwtWebSocketInterceptor())
                .withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/queue","/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");

    }


}
