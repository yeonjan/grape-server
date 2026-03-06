package pro.grape_server.domain.grape.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.grape_server.domain.grape.repository.RecordRepository;
import pro.grape_server.domain.grape.repository.GrapeRepository;
import pro.grape_server.global.exception.BusinessException;
import pro.grape_server.global.exception.ErrorCode;
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
                .orElseThrow(() -> new BusinessException(ErrorCode.GRAPE_NOT_FOUND));

        if (!grape.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.RECORD_CREATE_DENIED);
        }

        if (recordRepository.existsByGrapeIdAndRecordDate(grapeId, recordDate)) {
            throw new BusinessException(ErrorCode.RECORD_DUPLICATE);
        }

        Record record = Record.create(grape, grape.getUser(), memo, recordDate);
        recordRepository.save(record);

        return record.getId();
    }

    public void update(Long userId, Long recordId, String memo, LocalDate recordDate) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RECORD_NOT_FOUND));

        if (!record.getGrape().getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.RECORD_UPDATE_DENIED);
        }

        if (!record.getRecordDate().equals(recordDate)
                && recordRepository.existsByGrapeIdAndRecordDate(record.getGrape().getId(), recordDate)) {
            throw new BusinessException(ErrorCode.RECORD_DUPLICATE);
        }

        record.update(memo, recordDate);
    }

    public Record get(Long userId, Long recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RECORD_NOT_FOUND));

        if (!record.getGrape().getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.RECORD_ACCESS_DENIED);
        }

        return record;
    }

}
