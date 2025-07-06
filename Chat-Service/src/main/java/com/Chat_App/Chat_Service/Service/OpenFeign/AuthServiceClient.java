package com.Chat_App.Chat_Service.Service.OpenFeign;

import com.Chat_App.Chat_Service.Config.FeignClientAuthConfig;
import com.Chat_App.Chat_Service.DTO.Response.BasicUserProfile;
import com.Chat_App.Chat_Service.DTO.Response.UserList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "auth-service", configuration = FeignClientAuthConfig.class)
public interface AuthServiceClient {

    @GetMapping("/user/list")
    ResponseEntity<List<UserList>> getListOfAllUsers();

    @GetMapping("/user/profile")
    ResponseEntity<BasicUserProfile> getUserProfileData(@RequestParam("userId") UUID userId);

}
