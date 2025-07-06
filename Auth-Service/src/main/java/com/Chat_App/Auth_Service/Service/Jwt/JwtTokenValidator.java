package com.Chat_App.Auth_Service.Service.Jwt;

import com.Chat_App.Auth_Service.Models.User;
import com.Chat_App.Auth_Service.Repo.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JwtTokenValidator extends OncePerRequestFilter {

    private final UserRepo userRepo;

    @Autowired
    public JwtTokenValidator(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("In JwtTokenValidator");

        String path = request.getServletPath();

        System.out.println("Path is :- "+path);

        if(path.equals("/auth/login") || path.equals("/auth/register") || path.equals("/auth/forget-password")){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt = request.getHeader(JwtConstant.JWT_HEADER);

        System.out.println("Jwt is :- "+jwt);

        if(jwt != null && jwt.startsWith("Bearer ")){
            jwt = jwt.substring(7);

            try {
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECURITY_KEY.getBytes());

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                if (claims.getExpiration().before(new Date())) {
                    throw new ExpiredJwtException(null, claims, "JWT Token is expired");
                }

                String userId = String.valueOf(claims.get("userId"));

                User user = userRepo.getReferenceById(UUID.fromString(userId));

                if(user == null){
                    throw new UsernameNotFoundException("Invalid user");
                }

                String authority = String.valueOf(claims.get("authorities"));

                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);

                Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId(),null,authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }catch (ExpiredJwtException ex) {
                throw new InsufficientAuthenticationException("JWT Token is expired");
            } catch (JwtException | IllegalArgumentException ex) {
                throw new InsufficientAuthenticationException("Invalid JWT Token");
            }

        }else{
            throw new InsufficientAuthenticationException("Missing Jwt Token");
        }

        filterChain.doFilter(request,response);

    }
}
