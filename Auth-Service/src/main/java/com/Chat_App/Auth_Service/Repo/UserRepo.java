package com.Chat_App.Auth_Service.Repo;

import com.Chat_App.Auth_Service.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    User findByEmail(String userEmail);

}
