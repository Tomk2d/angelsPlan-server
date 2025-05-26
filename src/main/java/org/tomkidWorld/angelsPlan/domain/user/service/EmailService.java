package org.tomkidWorld.angelsPlan.domain.user.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {
    private final JavaMailSender emailSender;
    private final ConcurrentHashMap<String, String> verificationCodes;
    private static final long VERIFICATION_EXPIRY_TIME = 300000; // 5분
    private final ConcurrentHashMap<String, Long> verificationTimestamps;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
        this.verificationCodes = new ConcurrentHashMap<>();
        this.verificationTimestamps = new ConcurrentHashMap<>();
    }

    @Transactional
    public void sendVerificationEmail(String to) throws MessagingException {
        String verificationCode = generateVerificationCode();
        verificationCodes.put(to, verificationCode);
        verificationTimestamps.put(to, System.currentTimeMillis());

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom("noreply@angelsplan.com");
        helper.setTo(to);
        helper.setSubject("Angels Plan 이메일 인증");
        helper.setText("인증 코드: " + verificationCode + "\n\n이 코드는 5분간 유효합니다.");

        emailSender.send(message);
    }

    public boolean verifyEmail(String email, String code) {
        String savedCode = verificationCodes.get(email);
        Long timestamp = verificationTimestamps.get(email);
        
        if (savedCode == null || timestamp == null) {
            return false;
        }

        // 만료 시간 체크
        if (System.currentTimeMillis() - timestamp > VERIFICATION_EXPIRY_TIME) {
            verificationCodes.remove(email);
            verificationTimestamps.remove(email);
            return false;
        }

        if (savedCode.equals(code)) {
            verificationCodes.remove(email);
            verificationTimestamps.remove(email);
            return true;
        }

        return false;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
} 