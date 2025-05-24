package org.tomkidWorld.angelsPlan.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tomkidWorld.angelsPlan.domain.user.dto.LoginRequest;
import org.tomkidWorld.angelsPlan.domain.user.dto.SignUpRequest;
import org.tomkidWorld.angelsPlan.domain.user.entity.User;
import org.tomkidWorld.angelsPlan.domain.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password1234!");
        signUpRequest.setNickname("TestUser");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password1234!");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password1234!");
        user.setNickname("TestUser");
    }

    @Test
    @DisplayName("유효한 회원가입 요청시 성공한다")
    void signUp_WithValidRequest_ReturnsUser() throws Exception {
        // given
        given(userService.signUp(any(SignUpRequest.class))).willReturn(user);

        // when & then
        mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.nickname").value(user.getNickname()));
    }

    @Test
    @DisplayName("유효하지 않은 회원가입 요청시 400 에러를 반환한다")
    void signUp_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        // given
        signUpRequest.setEmail(null); // 이메일은 필수 필드

        // when & then
        mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유효한 로그인 요청시 성공한다")
    void login_WithValidCredentials_ReturnsUser() throws Exception {
        // given
        given(userService.login(any(LoginRequest.class))).willReturn(user);

        // when & then
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.nickname").value(user.getNickname()));
    }

    @Test
    @DisplayName("유효하지 않은 로그인 요청시 400 에러를 반환한다")
    void login_WithInvalidCredentials_ReturnsBadRequest() throws Exception {
        // given
        given(userService.login(any(LoginRequest.class)))
                .willThrow(new RuntimeException("비밀번호가 일치하지 않습니다."));

        // when & then
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("닉네임 중복 체크시 사용 가능한 닉네임이면 true를 반환한다")
    void checkNicknameAvailability_WithAvailableNickname_ReturnsTrue() throws Exception {
        // given
        String nickname = "availableNickname";
        given(userService.isNicknameExists(nickname)).willReturn(false);

        // when & then
        mockMvc.perform(get("/api/users/check-nickname")
                .param("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("닉네임 중복 체크시 이미 사용중인 닉네임이면 false를 반환한다")
    void checkNicknameAvailability_WithUnavailableNickname_ReturnsFalse() throws Exception {
        // given
        String nickname = "existingNickname";
        given(userService.isNicknameExists(nickname)).willReturn(true);

        // when & then
        mockMvc.perform(get("/api/users/check-nickname")
                .param("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }
} 