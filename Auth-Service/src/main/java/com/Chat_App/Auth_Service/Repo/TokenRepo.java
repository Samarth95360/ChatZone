package com.Chat_App.Auth_Service.Repo;

import com.Chat_App.Auth_Service.Models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TokenRepo extends JpaRepository<Token, UUID> {

    Token findByToken(String token);

    Token findByUserEmail(String userEmail);

}
