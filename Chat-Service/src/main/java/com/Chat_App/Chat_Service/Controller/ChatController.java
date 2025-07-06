package com.Chat_App.Chat_Service.Controller;

import com.Chat_App.Chat_Service.DTO.Request.Message;
import com.Chat_App.Chat_Service.DTO.Response.UserConversationData;
import com.Chat_App.Chat_Service.Repo.MessageRepo;
import com.Chat_App.Chat_Service.Service.Chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;

@Controller
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    @Autowired
    public ChatController(SimpMessagingTemplate template, ChatService chatService) {
        this.template = template;
        this.chatService = chatService;
    }

    @MessageMapping("/chat-message")
    public void handleSaveMessage(@Payload Message message, Principal principal){
        if(principal == null){
            System.out.println("Principal is null");
            return;
        }

        com.Chat_App.Chat_Service.DTO.Response.Message newMessage = chatService.saveMessage(message);
        if(newMessage == null){
            return;
        }

        System.out.println("New saved message is :- "+newMessage.toString());

        template.convertAndSend(
                "/topic/chat-room." + message.getChatRoomId(),
                newMessage
        );


    }

    @MessageMapping("/edit-message")
    public void handleEditMessage(@Payload com.Chat_App.Chat_Service.DTO.Response.Message message, Principal principal) {

        if(principal == null){
            System.out.println("Principal is null");
            return;
        }

        com.Chat_App.Chat_Service.Model.Message savedMessage = chatService.editMessage(message,principal);

        template.convertAndSend(
                "/topic/chat-room/edited-messages." + savedMessage.getChatRoomId(),
                message
        );

    }

    @MessageMapping("/delete-message")
    public void handleDeleteMessage(@Payload com.Chat_App.Chat_Service.DTO.Response.Message message , Principal principal){

        if(principal == null){
            System.out.println("Principal is null");
            return;
        }

        com.Chat_App.Chat_Service.Model.Message userMessage = chatService.deleteMessage(message , principal);

        com.Chat_App.Chat_Service.DTO.Response.Message send = com.Chat_App.Chat_Service.DTO.Response.Message.builder()
                        .id(userMessage.getId())
                        .sendBy(userMessage.getSendBy())
                        .text("This message was deleted")
                        .deleted(true)
                        .edited(userMessage.isEdited())
                        .dateTime(userMessage.getDateTime())
                        .build();


        template.convertAndSend(
                "/topic/chat-room/delete-messages." + userMessage.getChatRoomId(),
                send
        );

    }

    @MessageMapping("/update-profile")
    public void updateProfile(@Payload UserConversationData data,Principal principal){

        if(principal == null){
            System.out.println("Principal is null");
            return;
        }

        System.out.println("User conservation data is :- "+data.toString());

        template.convertAndSend(
                "/topic/conversations.updated."+data.getChatRoomId(),
                data
        );
    }



}
