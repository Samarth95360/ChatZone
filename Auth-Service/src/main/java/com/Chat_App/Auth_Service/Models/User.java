package com.Chat_App.Auth_Service.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        indexes = @Index(name = "idx_id" , columnList = "id")
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true,nullable = false , updatable = false)
    private UUID id;

    @Column(name = "full_name" , nullable = false,length = 50)
    private String fullName;

    @Column(name = "password" , nullable = false,unique = true)
    private String password;

    @Email
    @Column(name = "email" ,unique = true,nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role" , nullable = false)
    private Role role;

    @Column(name = "number_of_failed_login_attempt")
    private int failedLoginAttempt = 0;

    @Column(name = "account_locked")
    private boolean accountLocked = false;

    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "account_locked_time_stamp")
    private LocalDateTime timeStamp;

    @PrePersist
    public void userCreatedAt(){
        createdAt = LocalDateTime.now();
    }

}

