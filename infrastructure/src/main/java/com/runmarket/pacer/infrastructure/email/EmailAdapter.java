package com.runmarket.pacer.infrastructure.email;

import com.runmarket.pacer.domain.port.out.email.EmailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Override
    public void sendVerificationEmail(String to, String verificationLink) {
        if (mailPassword.isBlank()) {
            log.warn("[DEV] Verification link for {}: {}", to, verificationLink);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(from, "RunMarket", "UTF-8"));
            helper.setTo(to);
            helper.setSubject("[RunMarket] 이메일 인증을 완료해주세요");
            helper.setText(buildEmailBody(verificationLink), true);

            mailSender.send(message);
            log.info("Verification email sent to {}", to);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send verification email to {}", to, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String buildEmailBody(String verificationLink) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #333;">RunMarket 이메일 인증</h2>
                    <p>아래 버튼을 클릭하여 이메일 인증을 완료하고 회원가입을 마무리하세요.</p>
                    <p>이 링크는 30분 동안 유효합니다.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s"
                           style="background-color: #4CAF50; color: white; padding: 14px 28px;
                                  text-decoration: none; border-radius: 4px; font-size: 16px;">
                            이메일 인증하기
                        </a>
                    </div>
                    <p style="color: #888; font-size: 12px;">
                        본인이 요청하지 않은 경우 이 이메일을 무시하셔도 됩니다.
                    </p>
                </body>
                </html>
                """.formatted(verificationLink);
    }
}
