package com.ioob.backend.global.service;

import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Environment env;
    
    public void sendVerificationEmail(String to, String token) throws MessagingException {
        try {
            String subject = "Email Verification";
            StringBuilder messageBuilder = new StringBuilder();

            String confirmationUrl = env.getProperty("app.baseUrl") + "/api/auth/verify?token=" + token;

            // StringBuilder를 이용한 문자열 결합
            messageBuilder.append("<p>Please click the link below to verify your email:</p>")
                    .append("<a href=\"")
                    .append(confirmationUrl)
                    .append("\">Verify Email</a>");

            String message = messageBuilder.toString();  // 최종 문자열로 변환

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
