package pro.grape_server.domain.auth.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank(message = "Access token is required")
        String accessToken
) {
}
