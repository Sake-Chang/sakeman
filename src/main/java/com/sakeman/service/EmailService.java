package com.sakeman.service;

public interface EmailService {
    void sendUserVerificationEmail(String to, String verificationLink);
    void sendPasswordResetEmail(String to, String passwordResetLink);
    void sendNewEmailVerificationEmail(String to, String verificationLink);
}
