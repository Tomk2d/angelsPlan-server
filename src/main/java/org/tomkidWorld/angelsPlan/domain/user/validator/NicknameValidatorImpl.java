package org.tomkidWorld.angelsPlan.domain.user.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NicknameValidatorImpl implements ConstraintValidator<NicknameConstraint, String> {

    @Override
    public void initialize(NicknameConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        if (nickname == null) {
            return false;
        }

        // 한글 문자 개수 계산
        long koreanCount = nickname.chars()
                .filter(ch -> Character.UnicodeScript.of(ch) == Character.UnicodeScript.HANGUL)
                .count();

        // 영문 문자 개수 계산
        long englishCount = nickname.chars()
                .filter(ch -> (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
                .count();

        // 전체가 한글인 경우
        if (koreanCount == nickname.length()) {
            return nickname.length() <= 8;
        }
        // 전체가 영문이거나 혼합인 경우
        else {
            return nickname.length() <= 20;
        }
    }
} 