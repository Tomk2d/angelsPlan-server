package org.tomkidWorld.angelsPlan.global.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncoder {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH = 16;
    private static final String DELIMITER = ":";

    public static String encode(String password) {
        try {
            // Salt 생성
            byte[] salt = new byte[SALT_LENGTH];
            RANDOM.nextBytes(salt);
            String saltStr = Base64.getEncoder().encodeToString(salt);

            // 비밀번호 해싱
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            String hashedPasswordStr = Base64.getEncoder().encodeToString(hashedPassword);

            // salt:hashedPassword 형식으로 저장
            return saltStr + DELIMITER + hashedPasswordStr;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 암호화 중 오류가 발생했습니다.", e);
        }
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            // salt와 hashedPassword 분리
            String[] parts = encodedPassword.split(DELIMITER);
            if (parts.length != 2) {
                return false;
            }

            String saltStr = parts[0];
            String storedHashedPassword = parts[1];

            // 입력된 비밀번호 해싱
            byte[] salt = Base64.getDecoder().decode(saltStr);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(rawPassword.getBytes());
            String hashedPasswordStr = Base64.getEncoder().encodeToString(hashedPassword);

            // 해시된 비밀번호 비교
            return storedHashedPassword.equals(hashedPasswordStr);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 검증 중 오류가 발생했습니다.", e);
        }
    }
} 