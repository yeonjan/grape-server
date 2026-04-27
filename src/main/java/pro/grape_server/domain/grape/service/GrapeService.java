package pro.grape_server.domain.grape.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.grape_server.domain.auth.repository.UserRepository;
import pro.grape_server.domain.grape.repository.RecordRepository;
import pro.grape_server.domain.grape.repository.GrapeRepository;
import pro.grape_server.domain.grape.service.dto.GrapeOverviewResult;
import pro.grape_server.global.exception.BusinessException;
import pro.grape_server.global.exception.ErrorCode;
import pro.grape_server.model.entity.Grape;
import pro.grape_server.model.entity.Record;
import pro.grape_server.model.entity.User;
import pro.grape_server.model.entity.enums.GrapeStatus;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GrapeService {

    private final GrapeRepository grapeRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    public Long create(Long userId, String title, int targetCount, String reward) {
        if (grapeRepository.existsByUserIdAndStatus(userId, GrapeStatus.IN_PROGRESS)) {
            throw new BusinessException(ErrorCode.GRAPE_ALREADY_IN_PROGRESS);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Grape grape = Grape.create(user, title, targetCount, reward);
        grapeRepository.save(grape);
        return grape.getId();
    }

    public GrapeOverviewResult getOverview(Long userId, Long grapeId) {
        Grape grape = grapeRepository.findById(grapeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GRAPE_NOT_FOUND));

        if (!grape.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.GRAPE_ACCESS_DENIED);
        }

        List<Record> records = recordRepository.findAllByGrapeIdOrderByRecordDateAsc(grape.getId());
        return GrapeOverviewResult.from(grape, records);
    }

    public GrapeOverviewResult getInProgressGrapeOverView(Long userId) {
        Grape grape = grapeRepository.findByUserIdAndStatus(userId, GrapeStatus.IN_PROGRESS)
                .orElseThrow(() -> new BusinessException(ErrorCode.GRAPE_IN_PROGRESS_NOT_FOUND));

        List<Record> records = recordRepository.findAllByGrapeIdOrderByRecordDateAsc(grape.getId());
        return GrapeOverviewResult.from(grape, records);
    }

    public void update(Long userId, Long grapeId, String title, String reward, int targetCount) {
        Grape grape = grapeRepository.findById(grapeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GRAPE_NOT_FOUND));

        if (!grape.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.GRAPE_UPDATE_DENIED);
        }

        grape.update(title, reward, targetCount);
    }

    public boolean hasGrape(Long userId) {
        return grapeRepository.existsByUserId(userId);
    }
}
