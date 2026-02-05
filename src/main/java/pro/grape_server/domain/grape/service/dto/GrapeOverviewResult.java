package pro.grape_server.domain.grape.service.dto;

import pro.grape_server.model.entity.Grape;
import pro.grape_server.model.entity.Record;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public record GrapeOverviewResult(Long grapeId, String title, String reward, int targetCount, int totalCount,
                                  boolean todayRecorded,
                                  List<GrapeRecord> grapeRecords) {

    public record GrapeRecord(int index, Long recordId) {
    }

    public static GrapeOverviewResult from(Grape grape, List<Record> records) {
        List<GrapeRecord> grapeRecords = IntStream.range(0, records.size())
                .mapToObj(i -> new GrapeRecord(i + 1, records.get(i).getId()))
                .toList();

        boolean todayRecorded = !records.isEmpty() && records.getLast().getRecordDate().isEqual(LocalDate.now());

        return new GrapeOverviewResult(
                grape.getId(),
                grape.getTitle(),
                grape.getReward(),
                grape.getTargetCount(),
                grapeRecords.size(),
                todayRecorded,
                grapeRecords
        );
    }
}
