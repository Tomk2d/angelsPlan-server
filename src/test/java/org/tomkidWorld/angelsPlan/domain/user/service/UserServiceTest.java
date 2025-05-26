package org.tomkidWorld.angelsPlan.domain.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.tomkidWorld.angelsPlan.domain.user.dto.LoginRequest;
import org.tomkidWorld.angelsPlan.domain.user.dto.LoginResponse;
import org.tomkidWorld.angelsPlan.domain.user.dto.SignUpRequest;
import org.tomkidWorld.angelsPlan.domain.user.entity.User;
import org.tomkidWorld.angelsPlan.domain.user.repository.UserRepository;
import org.tomkidWorld.angelsPlan.global.error.BusinessException;
import org.tomkidWorld.angelsPlan.global.util.JwtUtil;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User user;
    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password123!");
        user.setNickname("testUser");

        signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password123!");
        signUpRequest.setNickname("testUser");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123!");
    }

    @Test
    @DisplayName("새로운 이메일로 회원가입 시 성공한다")
    void signUp_WithNewEmail_ReturnsUser() {
        // given
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(false);
        given(userRepository.existsByNickname(signUpRequest.getNickname())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        User result = userService.signUp(signUpRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(signUpRequest.getEmail());
        assertThat(result.getNickname()).isEqualTo(signUpRequest.getNickname());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시 예외가 발생한다")
    void signUp_WithExistingEmail_ThrowsException() {
        // given
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(signUpRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("올바른 로그인 정보로 로그인 시 성공한다")
    void login_WithValidCredentials_ReturnsUser() {
        // given
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(user));
        given(jwtUtil.generateToken(user.getId(), user.getEmail())).willReturn("test.jwt.token");

        // when
        LoginResponse result = userService.login(loginRequest);

        // then
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
        assertThat(result.getToken()).isEqualTo("test.jwt.token");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생한다")
    void login_WithInvalidEmail_ThrowsException() {
        // given
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 예외가 발생한다")
    void login_WithInvalidPassword_ThrowsException() {
        // given
        loginRequest.setPassword("wrongpassword");
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("존재하지 않는 닉네임 조회 시 false를 반환한다")
    void isNicknameExists_WithNonExistingNickname_ReturnsFalse() {
        // given
        String nickname = "nonexistent";
        given(userRepository.existsByNickname(nickname)).willReturn(false);

        // when
        boolean exists = userService.isNicknameExists(nickname);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("존재하는 닉네임 조회 시 true를 반환한다")
    void isNicknameExists_WithExistingNickname_ReturnsTrue() {
        // given
        String nickname = "testUser";
        given(userRepository.existsByNickname(nickname)).willReturn(true);

        // when
        boolean exists = userService.isNicknameExists(nickname);

        // then
        assertThat(exists).isTrue();
    }
} 