package pro.grape_server.domain.grape.service.dto;

import pro.grape_server.model.entity.Grape;
import pro.grape_server.model.entity.enums.GrapeStatus;

public record GrapeInfoResult(Long id, String title, int targetCount, String reward, GrapeStatus status,
                              int recordCount) {

    public static GrapeInfoResult from(Grape grape, int recordCount) {
        return new GrapeInfoResult(
                grape.getId(),
                grape.getTitle(),
                grape.getTargetCount(),
                grape.getReward(),
                grape.getStatus(),
                recordCount
        );
    }
}