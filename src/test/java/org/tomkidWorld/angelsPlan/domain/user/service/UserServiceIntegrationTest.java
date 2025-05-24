package org.tomkidWorld.angelsPlan.domain.user.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.tomkidWorld.angelsPlan.domain.user.dto.SignUpRequest;
import org.tomkidWorld.angelsPlan.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("동일한 닉네임으로 회원가입을 시도하면 예외가 발생한다")
    void signUpWithDuplicateNickname() {
        // given
        String duplicateNickname = "duplicateNickname";
        
        SignUpRequest request1 = new SignUpRequest();
        request1.setEmail("test1@test.com");
        request1.setPassword("password1234!");
        request1.setNickname(duplicateNickname);
        userService.signUp(request1);

        SignUpRequest request2 = new SignUpRequest();
        request2.setEmail("test2@test.com");
        request2.setPassword("password1234!");
        request2.setNickname(duplicateNickname);

        // when & then
        assertThatThrownBy(() -> userService.signUp(request2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미 존재하는 닉네임입니다");
    }

    @Test
    @DisplayName("캐시가 적용되어 두 번째 조회는 DB 접근 없이 결과를 반환한다")
    void checkNicknameCaching() {
        // given
        Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        String nickname = "cachedNickname";
        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@test.com");
        request.setPassword("password1234!");
        request.setNickname(nickname);
        userService.signUp(request);

        // when
        boolean firstCheck = userService.isNicknameExists(nickname);
        boolean secondCheck = userService.isNicknameExists(nickname);

        // then
        assertThat(firstCheck).isTrue();
        assertThat(secondCheck).isTrue();

        // 로그 검증
        long dbQueryCount = listAppender.list.stream()
                .filter(event -> event.getMessage().contains("닉네임 중복 체크 DB 조회"))
                .count();
        
        assertThat(dbQueryCount).isEqualTo(1); // DB 조회는 한 번만 발생해야 함
    }
} 