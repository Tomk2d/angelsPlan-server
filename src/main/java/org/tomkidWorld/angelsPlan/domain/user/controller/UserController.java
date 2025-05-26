package org.tomkidWorld.angelsPlan.domain.user.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tomkidWorld.angelsPlan.domain.user.dto.LoginRequest;
import org.tomkidWorld.angelsPlan.domain.user.dto.LoginResponse;
import org.tomkidWorld.angelsPlan.domain.user.dto.SignUpRequest;
import org.tomkidWorld.angelsPlan.domain.user.dto.VerifyEmailRequest;
import org.tomkidWorld.angelsPlan.domain.user.entity.User;
import org.tomkidWorld.angelsPlan.domain.user.service.EmailService;
import org.tomkidWorld.angelsPlan.domain.user.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/email/verify/send")
    public ResponseEntity<Void> sendVerificationEmail(@RequestParam String email) {
        try {
            emailService.sendVerificationEmail(email);
            return ResponseEntity.ok().build();
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/email/verify")
    public ResponseEntity<Boolean> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        boolean isVerified = emailService.verifyEmail(request.getEmail(), request.getCode());
        return ResponseEntity.ok(isVerified);
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
