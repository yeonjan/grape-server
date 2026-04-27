package pro.grape_server.domain.grape.controller.dto.request;

public record UpdateGrapeRequest(String title, String reward, int targetCount) {
}
