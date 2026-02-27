package pro.grape_server.domain.auth.controller.dto.response;

public record GuestLoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        String deviceHash
) {
    public static GuestLoginResponse of(String accessToken, String refreshToken, String deviceHash) {
        return new GuestLoginResponse(accessToken, refreshToken, "Bearer", deviceHash);
    }
}
