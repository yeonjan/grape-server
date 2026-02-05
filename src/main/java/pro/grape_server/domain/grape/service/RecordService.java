package pro.grape_server.domain.grape.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.grape_server.domain.grape.repository.RecordRepository;
import pro.grape_server.domain.grape.repository.GrapeRepository;
import pro.grape_server.model.entity.Grape;
import pro.grape_server.model.entity.Record;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    private final GrapeRepository grapeRepository;
    private final RecordRepository recordRepository;

    public Long create(Long userId, Long grapeId, String memo, LocalDate recordDate) {
        Grape grape = grapeRepository.findById(grapeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포도입니다."));

        if (!grape.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 포도에만 기록을 추가할 수 있습니다.");
        }

        if (recordRepository.existsByGrapeIdAndRecordDate(grapeId, recordDate)) {
            throw new IllegalStateException("하루에 하나의 포도알만 추가할 수 있습니다.");
        }

        Record record = Record.create(grape, grape.getUser(), memo, recordDate);
        recordRepository.save(record);

        return record.getId();
    }

    public Record get(Long userId, Long recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다."));

        if (!record.getGrape().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 기록만 조회할 수 있습니다.");
        }

        return record;
    }

}
