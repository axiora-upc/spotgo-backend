package com.axiora.spotgo.iam.infrastructure.notifications;

import com.axiora.spotgo.iam.application.PasswordResetNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpPasswordResetNotificationService implements PasswordResetNotificationService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public SmtpPasswordResetNotificationService(JavaMailSender mailSender,
                                                @Value("${app.password-reset.from-email:}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Override
    public void sendPasswordResetCode(String email, String code) {
        var message = new SimpleMailMessage();
        if (fromEmail != null && !fromEmail.isBlank()) {
            message.setFrom(fromEmail);
        }
        message.setTo(email);
        message.setSubject("SpotGo password reset code");
        message.setText("Your SpotGo password reset code is: " + code + "\n\nThis code can be used only once and expires soon.");
        mailSender.send(message);
    }
}
