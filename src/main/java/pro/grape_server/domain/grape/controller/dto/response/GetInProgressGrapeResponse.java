package pro.grape_server.domain.grape.controller.dto.response;

import pro.grape_server.domain.grape.service.dto.GrapeInfoResult;
import pro.grape_server.model.entity.enums.GrapeStatus;

public record GetInProgressGrapeResponse(Long id, String title, int targetCount, String reward, GrapeStatus status,
                                         int recordCount) {

    public static GetInProgressGrapeResponse from(GrapeInfoResult result) {
        return new GetInProgressGrapeResponse(
                result.id(),
                result.title(),
                result.targetCount(),
                result.reward(),
                result.status(),
                result.recordCount()
        );
    }
}