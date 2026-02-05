package pro.grape_server.domain.grape.controller.dto.response;

import pro.grape_server.domain.grape.service.dto.GrapeOverviewResult;

import java.util.List;

public record GetGrapeResponse(Long grapeId, String title, String reward, int targetCount, int totalCount,
                               boolean todayRecorded,
                               List<GrapeOverviewResult.GrapeRecord> records) {
    public static GetGrapeResponse from(GrapeOverviewResult result) {
        return new GetGrapeResponse(
                result.grapeId(),
                result.title(),
                result.reward(),
                result.targetCount(),
                result.totalCount(),
                result.todayRecorded(),
                result.grapeRecords()
        );
    }
}
