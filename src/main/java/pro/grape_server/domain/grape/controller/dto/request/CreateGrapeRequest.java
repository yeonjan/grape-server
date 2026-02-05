package pro.grape_server.domain.grape.controller.dto.request;

public record CreateGrapeRequest(String title, int targetCount, String reward) {
}
