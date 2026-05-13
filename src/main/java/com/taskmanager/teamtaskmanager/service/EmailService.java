package com.taskmanager.teamtaskmanager.service;

public interface EmailService {

    void sendOtp(String to, String otp);
    void sendResetPasswordOtp(String to, String otp);
}
