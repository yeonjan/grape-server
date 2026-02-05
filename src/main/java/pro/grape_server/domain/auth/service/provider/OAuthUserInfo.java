package pro.grape_server.domain.auth.service.provider;

public record OAuthUserInfo(
        String providerUserId,
        String name,
        String email
) {
}
