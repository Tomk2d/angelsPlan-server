package org.tomkidWorld.angelsPlan.domain.user.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class NicknameValidator {
    private static final int MAX_KOREAN_LENGTH = 8;
    private static final int MAX_ENGLISH_LENGTH = 20;
    private static final Pattern KOREAN_PATTERN = Pattern.compile("^[가-힣]+$");
    private static final Pattern ENGLISH_PATTERN = Pattern.compile("^[a-zA-Z]+$");
    private static final Pattern VALID_CHARS_PATTERN = Pattern.compile("^[a-zA-Z가-힣]+$");

    public boolean isValid(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return false;
        }

        if (!VALID_CHARS_PATTERN.matcher(nickname).matches()) {
            return false;
        }

        // 한글로만 구성된 경우
        if (KOREAN_PATTERN.matcher(nickname).matches()) {
            return nickname.length() <= MAX_KOREAN_LENGTH;
        }

        // 영문으로만 구성된 경우
        if (ENGLISH_PATTERN.matcher(nickname).matches()) {
            return nickname.length() <= MAX_ENGLISH_LENGTH;
        }

        // 한글과 영문이 혼합된 경우
        return nickname.length() <= MAX_ENGLISH_LENGTH;
    }
} 