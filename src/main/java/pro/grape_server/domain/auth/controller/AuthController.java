package pro.grape_server.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pro.grape_server.domain.auth.controller.dto.request.GuestLoginRequest;
import pro.grape_server.domain.auth.controller.dto.request.RefreshTokenRequest;
import pro.grape_server.domain.auth.controller.dto.request.SocialLoginRequest;
import pro.grape_server.domain.auth.controller.dto.response.LoginResponse;
import pro.grape_server.domain.auth.service.AuthService;
import pro.grape_server.global.security.CustomUserDetails;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register/guest")
    public ResponseEntity<LoginResponse> guestSignUp(
            @Valid @RequestBody GuestLoginRequest request
    ) {
        LoginResponse response = authService.guestSignUp(request.deviceId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/guest")
    public ResponseEntity<LoginResponse> guestLogin(
            @Valid @RequestBody GuestLoginRequest request
    ) {
        LoginResponse response = authService.guestLogin(request.deviceId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable String provider,
            @Valid @RequestBody SocialLoginRequest request,
            Authentication authentication
    ) {
        Long guestUserId = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            if (userDetails.getUser().isGuest()) {
                guestUserId = userDetails.getUserId();
            }
        }
        LoginResponse response = authService.login(provider, request.token(), guestUserId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        LoginResponse response = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(response);
    }
}
