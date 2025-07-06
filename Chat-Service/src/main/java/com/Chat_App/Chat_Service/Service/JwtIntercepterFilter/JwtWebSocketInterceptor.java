package com.Chat_App.Chat_Service.Service.JwtIntercepterFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.crypto.SecretKey;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtWebSocketInterceptor implements HandshakeInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JwtWebSocketInterceptor.class);
    private final SecretKey key = Keys.hmacShaKeyFor(JwtConst.SECURITY_KEY.getBytes());

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        logger.info("WebSocket handshake interceptor invoked: {}", request.getURI().getPath());
        System.out.println("WebSocket handshake interceptor invoked: {}" + request.getURI().getPath());

//         Check if user ID has been added by the API Gateway
        List<String> userIdHeaders = request.getHeaders().get("X-User-Id");
        if (userIdHeaders != null && !userIdHeaders.isEmpty()) {
            String userId = userIdHeaders.get(0);
            attributes.put("userId", userId);
            attributes.put("principal", new UserPrincipal(userId));
            logger.info("User ID found in header: {}", userId);
            return true;
        }

//         For SockJS info requests, allow without authentication
        if (request.getURI().getPath().contains("/info")) {
            logger.info("SockJS info request - bypassing authentication");
            return true;
        }

        // Extract from token in query parameters
        String query = request.getURI().getQuery();

        if (query == null || !query.contains("token=")) {
            logger.warn("No token found in query parameters");
            return false;
        }

        // Parse query to extract token
        String token = null;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            if (pair.startsWith("token=")) {
                token = URLDecoder.decode(pair.substring("token=".length()), StandardCharsets.UTF_8);
                break;
            }
        }

        if (token == null) {
            logger.warn("Token extraction failed");
            return false;
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                logger.warn("Token expired");
                System.out.println("Token expired");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            String userId = String.valueOf(claims.get("userId"));
            // Store user info in the WebSocket session attributes
            attributes.put("userId", userId);
            attributes.put("principal", new UserPrincipal(userId));
//            logger.info("WebSocket handshake successful for user: {}", userId);
            return true;
        } catch (JwtException e) {
            logger.error("JWT validation error: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // If there was an exception during handshake, log it
        if (exception != null) {
            logger.error("Exception during handshake: {}", exception.getMessage(), exception);
        }
    }
}