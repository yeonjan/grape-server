package pro.grape_server.domain.grape.controller.dto.response;

import pro.grape_server.model.entity.Record;

import java.time.LocalDate;

public record GetRecordResponse(String memo, LocalDate recordDate) {
    public static GetRecordResponse from(Record record) {
        return new GetRecordResponse(record.getMemo(), record.getRecordDate());
    }
}
