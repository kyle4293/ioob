package com.ioob.backend.service;

import com.ioob.backend.entity.VerificationToken;
import jakarta.mail.MessagingException;

import java.util.Calendar;
import java.util.Optional;

public interface EmailService {
    void sendVerificationEmail(String to, String token) throws MessagingException;
    boolean verifyToken(String token);
}
