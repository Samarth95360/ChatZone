package com.Chat_App.Chat_Service.Service.JwtIntercepterFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {

    private final SecretKey key;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> AUTH_PATH_PATTERNS = List.of("/user/**", "/ws-chat/**","/chat/**");

    @Autowired
    public JwtTokenValidator() {
        this.key = Keys.hmacShaKeyFor(JwtConst.SECURITY_KEY.getBytes());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String query = request.getQueryString();

        // Check if path matches any pattern
        boolean requiresAuth = AUTH_PATH_PATTERNS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (!requiresAuth) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("Request :- ");
        System.out.println("socket path :- "+path);
        System.out.println("Query is :- "+query);

        // Try header first
        String jwt = null;
        String authHeader = request.getHeader(JwtConst.JWT_HEADER);
//        System.out.println("authHeader ;- "+authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("Jwt inside is :- "+jwt);
        }
        // If no header, try query param ?token=...
        else if (query != null && query.contains("token=")) {
            for (String pair : query.split("&")) {
                if (pair.startsWith("token=")) {
                    jwt = URLDecoder.decode(pair.substring(6), StandardCharsets.UTF_8);
                    break;
                }
            }
        }
        System.out.println("Jwt is :- "+jwt);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                logger.info("Expired jwt token");
                System.out.println("Expired jwt token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is expired");
                return;
            }

            String userId = String.valueOf(claims.get("userId"));

            String authority = String.valueOf(claims.get("authorities"));
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);

            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, jwt, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is expired");
            return;
        } catch (JwtException | IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
