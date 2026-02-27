package pro.grape_server.domain.grape.controller.dto.response;

public record GetUserEmailResponse(String email) {

    public static GetUserEmailResponse from(String email) {
        return new GetUserEmailResponse(email);
    }
}
