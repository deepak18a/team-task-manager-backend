package com.taskmanager.teamtaskmanager.service;

import com.taskmanager.teamtaskmanager.dto.request.*;
import com.taskmanager.teamtaskmanager.dto.response.AuthResponse;
import com.taskmanager.teamtaskmanager.dto.response.UserResponse;

public interface AuthService {

    UserResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    String verifyOtp(VerifyOtpRequest request);
    String forgotPassword(ForgotPasswordRequest request);
    String resetPassword(ResetPasswordRequest request);
}
