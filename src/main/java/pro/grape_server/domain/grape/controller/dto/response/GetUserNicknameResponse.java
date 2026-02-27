package pro.grape_server.domain.grape.controller.dto.response;

public record GetUserNicknameResponse(String nickname) {

    public static GetUserNicknameResponse from(String nickname) {
        return new GetUserNicknameResponse(nickname);
    }
}
