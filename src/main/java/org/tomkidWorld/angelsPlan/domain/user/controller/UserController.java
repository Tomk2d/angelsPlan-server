package org.tomkidWorld.angelsPlan.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tomkidWorld.angelsPlan.domain.user.dto.*;
import org.tomkidWorld.angelsPlan.domain.user.entity.User;
import org.tomkidWorld.angelsPlan.domain.user.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameAvailabilityResponse> checkNicknameAvailability(@RequestParam String nickname) {
        boolean isAvailable = !userService.isNicknameExists(nickname);
        return ResponseEntity.ok(new NicknameAvailabilityResponse(isAvailable));
    }

    public static class NicknameAvailabilityResponse {
        private final boolean available;

        public NicknameAvailabilityResponse(boolean available) {
            this.available = available;
        }

        public boolean isAvailable() {
            return available;
        }
    }
} 
 