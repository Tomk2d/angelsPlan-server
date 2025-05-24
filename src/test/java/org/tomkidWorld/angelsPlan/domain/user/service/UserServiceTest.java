package org.tomkidWorld.angelsPlan.domain.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tomkidWorld.angelsPlan.domain.user.dto.LoginRequest;
import org.tomkidWorld.angelsPlan.domain.user.dto.SignUpRequest;
import org.tomkidWorld.angelsPlan.domain.user.entity.User;
import org.tomkidWorld.angelsPlan.domain.user.repository.UserRepository;

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

    @InjectMocks
    private UserService userService;

    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password");
        signUpRequest.setName("Test User");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setName("Test User");
    }

    @Test
    void signUp_WithNewEmail_ReturnsUser() {
        // given
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        User result = userService.signUp(signUpRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(signUpRequest.getEmail());
        assertThat(result.getName()).isEqualTo(signUpRequest.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signUp_WithExistingEmail_ThrowsException() {
        // given
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(signUpRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    void login_WithValidCredentials_ReturnsUser() {
        // given
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(user));

        // when
        User result = userService.login(loginRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(loginRequest.getEmail());
    }

    @Test
    void login_WithInvalidEmail_ThrowsException() {
        // given
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("존재하지 않는 이메일입니다.");
    }

    @Test
    void login_WithInvalidPassword_ThrowsException() {
        // given
        loginRequest.setPassword("wrongpassword");
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }
} 