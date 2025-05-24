package org.tomkidWorld.angelsPlan.domain.user.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidatorImpl implements ConstraintValidator<PasswordConstraint, String> {
    
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
    
    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // 길이 검사 (10자 이상)
        if (password.length() < 10) {
            addConstraintViolation(context, "비밀번호는 10자 이상이어야 합니다.");
            return false;
        }

        // 영문 포함 검사
        if (!LETTER_PATTERN.matcher(password).find()) {
            addConstraintViolation(context, "비밀번호는 영문을 포함해야 합니다.");
            return false;
        }

        // 숫자 포함 검사
        if (!DIGIT_PATTERN.matcher(password).find()) {
            addConstraintViolation(context, "비밀번호는 숫자를 포함해야 합니다.");
            return false;
        }

        // 특수문자 포함 검사
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            addConstraintViolation(context, "비밀번호는 특수문자를 포함해야 합니다.");
            return false;
        }

        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
} 