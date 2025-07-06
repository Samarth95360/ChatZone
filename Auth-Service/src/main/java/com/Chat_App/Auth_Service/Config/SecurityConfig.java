package com.Chat_App.Auth_Service.Config;

import com.Chat_App.Auth_Service.Repo.UserRepo;
import com.Chat_App.Auth_Service.Service.Jwt.JwtTokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    private final UserRepo userRepo;

    @Autowired
    public SecurityConfig(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        return http
                .csrf(csrf -> csrf.disable())
                .securityContext(securityContext -> securityContext.requireExplicitSave(false))
                .sessionManagement(
                        manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).authorizeHttpRequests(
                        Authorize -> Authorize
                                .requestMatchers("/auth/login","/auth/register","/auth/forget-password").permitAll()
                                .requestMatchers("/auth/otp-verification","/auth/resend-otp").hasAnyRole("OTP","TOKEN")
                                .requestMatchers("/auth/new-password").hasRole("PASSWORD")
                                .requestMatchers("/user/list","/user/profile").hasAnyRole("USER")
                                .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtTokenValidator(userRepo), BasicAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .httpBasic(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults())
                .build();

    }

    private CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",   //  React Development URL
                "http://localhost:3001",   //  Your Production URL (once deployed)
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:8000"
        ));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type"
        ));
        corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
