package pro.grape_server.domain.grape.controller.dto.request;

import java.time.LocalDate;

public record CreateRecordRequest(Long grapeId, String memo, LocalDate recordDate) {
}
