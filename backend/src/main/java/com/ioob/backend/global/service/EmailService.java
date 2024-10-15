package com.ioob.backend.service;

import com.ioob.backend.entity.VerificationToken;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;
import com.ioob.backend.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Calendar;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Environment env;
    private final VerificationTokenRepository tokenRepository;

    
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


    
    public boolean verifyToken(String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);  // 토큰이 존재하지 않으면 CustomException 발생
        }

        VerificationToken verificationToken = optionalToken.get();

        Calendar cal = Calendar.getInstance();
        if (verificationToken.getExpiryDate().before(cal.getTime())) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);  // 토큰이 만료되었을 때 CustomException 발생
        }

        // 토큰이 유효하면 사용자 계정을 활성화
        verificationToken.getUser().verified();
        tokenRepository.delete(verificationToken); // 인증된 토큰 삭제 (선택적)

        return true;
    }
}
