package com.Chat_App.Auth_Service.Service.Jwt;

import com.Chat_App.Auth_Service.Models.Role;
import com.Chat_App.Auth_Service.Models.User;
import com.Chat_App.Auth_Service.Repo.UserRepo;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtProvider {

    private final UserRepo userRepo;

    private final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECURITY_KEY.getBytes());

    @Autowired
    public JwtProvider(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public String jwtTokenGenerator(Authentication authentication){
        String email = authentication.getName();

        User user = userRepo.findByEmail(email);

        if(user == null){
            throw new IllegalArgumentException("User Not Found");
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String role = populateRoles(authorities);

        Instant tokenExpiryTime = role.equals(Role.ROLE_OTP.toString()) || role.equals(Role.ROLE_TOKEN.toString()) || role.equals(Role.ROLE_PASSWORD.toString())
                ? Instant.now().plusSeconds(300)
                : Instant.now().plusSeconds(86400);

        return Jwts.builder()
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(tokenExpiryTime))
                .claim("userId",user.getId())
                .claim("authorities", role)
                .signWith(key)
                .compact();

    }

    private String populateRoles(Collection<? extends GrantedAuthority> authorities) {
        Set<String> roles = new HashSet<>();
        for (GrantedAuthority authority : authorities){
            roles.add(authority.getAuthority());
        }
        return String.join(",",roles);
    }

}
