package com.Chat_App.Auth_Service.Utils;

import com.Chat_App.Auth_Service.DTO.request.TokenDTO;
import com.Chat_App.Auth_Service.Models.Token;
import com.Chat_App.Auth_Service.Repo.TokenRepo;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TokenUtils {

    private final TokenRepo tokenRepo;
//    private final EmailService emailService;

    private static final int TOKEN_VALID_DURATION = 5;
    private final ConcurrentHashMap<String, LocalDateTime> tokenStore = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;
    private static final int TOKEN_CHECK_INTERVAL = 1; // Interval in minutes to check for expired tokens


    @Autowired
    public TokenUtils(TokenRepo tokenRepo) {
        this.tokenRepo = tokenRepo;
//        this.emailService = emailService;
    }

    public boolean createToken(String userEmail){

        // Check if a token already exists for the user
        Token existingToken = tokenRepo.findByUserEmail(userEmail);
        if (existingToken != null) {
            tokenRepo.delete(existingToken);
            tokenStore.remove(existingToken.getToken());
        }

        String token = UUID.randomUUID().toString().replaceAll("-","").substring(0,6);
        try {
//            emailService.senMail(userEmail, "Verification Token", "Dear User Your verification Token is :- " + token);
            Token token1 = new Token();
            token1.setToken(token);
            token1.setUserEmail(userEmail);
            tokenRepo.save(token1);
            tokenStore.put(token, LocalDateTime.now().plusMinutes(TOKEN_VALID_DURATION));
            System.out.println("Token for user "+userEmail+" is :- "+token);
            if(scheduler == null || scheduler.isShutdown()){
                startTokenRemovalPeriodically();
            }
            return true;
        }catch (Exception ex){
            System.err.println("Failed to send email to: " + userEmail + ". Error: " + ex.getMessage());
            return false;
        }
    }

    public TokenDTO tokenVerification(String token){
        Token existingToken = tokenRepo.findByToken(token);
        if(existingToken != null){
            if(existingToken.getIssueTime().plusMinutes(TOKEN_VALID_DURATION).isAfter(LocalDateTime.now())) {
                if (existingToken.getToken().equals(token)) {
                    TokenDTO response = new TokenDTO(true, existingToken.getUserEmail());
                    tokenStore.remove(token);
                    tokenRepo.delete(existingToken);

                    if(tokenStore.isEmpty()){
                        shutDownScheduler();
                    }

                    return response;
                }
            }
        }
        return null;
    }

    private synchronized void startTokenRemovalPeriodically() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(1);
            System.out.println("Starting token scheduler...");

            scheduler.scheduleAtFixedRate(() -> {
                LocalDateTime now = LocalDateTime.now();
                System.out.println("Checking for expired tokens at: " + now);

                tokenStore.forEach((token, expirationTime) -> {
                    if (expirationTime.isBefore(now)) {
                        Token tokenObj = tokenRepo.findByToken(token);
                        if (tokenObj != null) {
                            tokenRepo.delete(tokenObj);
                        }
                        tokenStore.remove(token);
                        System.out.println("Removed expired token: " + token);
                    }
                });

                // Shut down the scheduler if the token store is empty
                if (tokenStore.isEmpty()) {
                    System.out.println("Token store is empty. Shutting down scheduler...");
                    shutDownScheduler();
                }
            }, 0, TOKEN_CHECK_INTERVAL, TimeUnit.MINUTES);
        }
    }

    private void shutDownScheduler(){
        if(scheduler != null && !scheduler.isShutdown()){
            scheduler.shutdown();
            System.out.println("Token scheduler shut down.");
        }
    }

}
