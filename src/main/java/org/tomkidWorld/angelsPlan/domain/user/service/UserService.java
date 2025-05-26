package org.tomkidWorld.angelsPlan.domain.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tomkidWorld.angelsPlan.domain.user.dto.LoginRequest;
import org.tomkidWorld.angelsPlan.domain.user.dto.LoginResponse;
import org.tomkidWorld.angelsPlan.domain.user.dto.SignUpRequest;
import org.tomkidWorld.angelsPlan.domain.user.entity.User;
import org.tomkidWorld.angelsPlan.domain.user.repository.UserRepository;
import org.tomkidWorld.angelsPlan.global.error.BusinessException;
import org.tomkidWorld.angelsPlan.global.error.ErrorCode;
import org.tomkidWorld.angelsPlan.global.util.JwtUtil;

@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    @CacheEvict(value = "nicknameExists", allEntries = true) // 회원가입 시 캐시 전체 삭제
    public User signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATION);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATION);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());  // 실제 프로젝트에서는 반드시 암호화 필요
        user.setNickname(request.getNickname());

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        logger.info("로그인 시도 - 이메일: {}", request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        return LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .token(token)
                .build();
    }

    @Cacheable(value = "nicknameExists", key = "#nickname", unless = "#result == false")
    @Transactional(readOnly = true)
    public boolean isNicknameExists(String nickname) {
        logger.info("닉네임 중복 체크 DB 조회 - 닉네임: {}", nickname);
        return userRepository.existsByNickname(nickname);
    }
} 