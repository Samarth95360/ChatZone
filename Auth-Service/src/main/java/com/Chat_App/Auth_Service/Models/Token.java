package com.Chat_App.Auth_Service.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "token")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id" , unique = true , nullable = false,updatable = false)
    private UUID id;

    @Column(name = "token" , unique = true,nullable = false,updatable = false)
    private String token;

    @Email
    @Column(name = "user_email" , unique = true,nullable = false,updatable = false)
    private String userEmail;

    @Column(name = "issuer_time")
    private LocalDateTime issueTime;

    @PrePersist
    public void tokenCreatedAt(){
        this.issueTime = LocalDateTime.now();
    }


}
