package com.Chat_App.Auth_Service.Controller;

import com.Chat_App.Auth_Service.DTO.request.*;
import com.Chat_App.Auth_Service.DTO.response.LoginResponse;
import com.Chat_App.Auth_Service.DTO.response.RegisterResponse;
import com.Chat_App.Auth_Service.Service.ForgetPassword.ForgetPasswordService;
import com.Chat_App.Auth_Service.Service.Login.UserLoginService;
import com.Chat_App.Auth_Service.Service.Otp.OtpVerificationService;
import com.Chat_App.Auth_Service.Service.Registration.UserRegistrationService;
import com.Chat_App.Auth_Service.Service.ResendOtp.ResendOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRegistrationService userRegistrationService;
    private final UserLoginService userLoginService;
    private final OtpVerificationService otpVerificationService;
    private final ForgetPasswordService forgetPasswordService;
    private final ResendOtpService resendOtpService;

    @Autowired
    public AuthController(UserRegistrationService userRegistrationService, UserLoginService userLoginService, OtpVerificationService otpVerificationService, ForgetPasswordService forgetPasswordService, ResendOtpService resendOtpService){
        this.userRegistrationService = userRegistrationService;
        this.userLoginService = userLoginService;
        this.otpVerificationService = otpVerificationService;
        this.forgetPasswordService = forgetPasswordService;
        this.resendOtpService = resendOtpService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest registerRequest){
        return userRegistrationService.registerUser(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest login){
        return userLoginService.loginUser(login);
    }

    @PreAuthorize("hasAnyRole('OTP','TOKEN')")
    @PostMapping("/otp-verification")
    public ResponseEntity<LoginResponse> otpVerification(@RequestBody OtpRequest request) {
        return otpVerificationService.verifyOtp(request.getOtp());
    }

    @PreAuthorize("hasAnyRole('OTP','TOKEN')")
    @GetMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(){
        return resendOtpService.resendOtp();
    }

    @PostMapping("/forget-password")
    public ResponseEntity<LoginResponse> forgetPassword(@RequestBody ForgetPasswordEmail email){
        return forgetPasswordService.forgetPasswordEmail(email.getEmail());
    }

    @PreAuthorize("hasRole('PASSWORD')")
    @PostMapping("/new-password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatedPassword updatedPassword){
        return forgetPasswordService.validatePasswordAndChange(updatedPassword);
    }

}
