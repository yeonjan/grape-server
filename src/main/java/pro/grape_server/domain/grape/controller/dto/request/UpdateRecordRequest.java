package pro.grape_server.domain.grape.controller.dto.request;

import java.time.LocalDate;

public record UpdateRecordRequest(String memo, LocalDate recordDate) {
}
