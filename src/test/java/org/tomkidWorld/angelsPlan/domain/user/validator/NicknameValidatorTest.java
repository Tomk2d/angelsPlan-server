package org.tomkidWorld.angelsPlan.domain.user.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NicknameValidatorTest {

    private final NicknameValidator validator = new NicknameValidator();

    @Test
    @DisplayName("한글 닉네임이 8자 이하일 때 유효성 검사를 통과한다")
    void validateKoreanNickname() {
        // given
        String validKoreanNickname = "안녕하세요";

        // when
        boolean isValid = validator.isValid(validKoreanNickname);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("한글 닉네임이 8자를 초과할 때 유효성 검사를 실패한다")
    void validateTooLongKoreanNickname() {
        // given
        String tooLongKoreanNickname = "안녕하세요반갑습니다";

        // when
        boolean isValid = validator.isValid(tooLongKoreanNickname);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("영문 닉네임이 20자 이하일 때 유효성 검사를 통과한다")
    void validateEnglishNickname() {
        // given
        String validEnglishNickname = "helloworld";

        // when
        boolean isValid = validator.isValid(validEnglishNickname);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("영문 닉네임이 20자를 초과할 때 유효성 검사를 실패한다")
    void validateTooLongEnglishNickname() {
        // given
        String tooLongEnglishNickname = "thisisaverylongnicknameover20characters";

        // when
        boolean isValid = validator.isValid(tooLongEnglishNickname);

        // then
        assertThat(isValid).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("빈 문자열이나 공백만 있는 닉네임은 유효성 검사를 실패한다")
    void validateEmptyOrBlankNickname(String emptyOrBlankNickname) {
        // when
        boolean isValid = validator.isValid(emptyOrBlankNickname);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("한글과 영문이 혼합된 닉네임이 20자 이하일 때 유효성 검사를 통과한다")
    void validateMixedNickname() {
        // given
        String validMixedNickname = "안녕hello";

        // when
        boolean isValid = validator.isValid(validMixedNickname);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("특수문자가 포함된 닉네임은 유효성 검사를 실패한다")
    void validateNicknameWithSpecialCharacters() {
        // given
        String nicknameWithSpecialChars = "hello!@#";

        // when
        boolean isValid = validator.isValid(nicknameWithSpecialChars);

        // then
        assertThat(isValid).isFalse();
    }
} 