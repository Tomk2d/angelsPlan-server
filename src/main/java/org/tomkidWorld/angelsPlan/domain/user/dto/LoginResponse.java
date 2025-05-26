package org.tomkidWorld.angelsPlan.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private Long id;
    private String email;
    private String nickname;
    private String token;
} 