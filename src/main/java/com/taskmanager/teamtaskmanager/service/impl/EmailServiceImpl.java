package com.taskmanager.teamtaskmanager.service.impl;

import com.taskmanager.teamtaskmanager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {


    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendOtp(String to, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Team Task Manager Email Verification");
        message.setText("Your OTP is: " + otp + "\nThis OTP is valid for 5 minutes.");

        mailSender.send(message);
    }

    @Override
    public void sendResetPasswordOtp(String to, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Team Task Manager Reset Password");
        message.setText("Your password reset OTP is: " + otp +
                "\nThis OTP is valid for 5 minutes.");

        mailSender.send(message);
    }
}
