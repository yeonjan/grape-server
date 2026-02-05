package pro.grape_server.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.grape_server.domain.auth.controller.dto.request.RefreshTokenRequest;
import pro.grape_server.domain.auth.controller.dto.request.SocialLoginRequest;
import pro.grape_server.domain.auth.controller.dto.response.LoginResponse;
import pro.grape_server.domain.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable String provider,
            @Valid @RequestBody SocialLoginRequest request
    ) {
        LoginResponse response = authService.login(provider, request.accessToken());
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
