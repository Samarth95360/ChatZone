package com.Chat_App.Chat_Service.Controller;

import com.Chat_App.Chat_Service.DTO.Request.CallInitiateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CallController {

    private final SimpMessagingTemplate template;

    @Autowired
    public CallController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/audio-call-initiate")
    public void handleCallInitiateRequest(@Payload CallInitiateRequest request){
        System.out.println("Call request is :- "+request.toString());
        template.convertAndSend(
                "/topic/audio-call.initiate."+request.getReceiverId(),
                request
        );
    }

}
