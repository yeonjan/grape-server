package pro.grape_server.domain.grape.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.grape_server.domain.auth.repository.UserRepository;
import pro.grape_server.domain.grape.repository.RecordRepository;
import pro.grape_server.domain.grape.repository.GrapeRepository;
import pro.grape_server.domain.grape.service.dto.GrapeInfoResult;
import pro.grape_server.domain.grape.service.dto.GrapeOverviewResult;
import pro.grape_server.global.exception.BusinessException;
import pro.grape_server.global.exception.ErrorCode;
import pro.grape_server.model.entity.Grape;
import pro.grape_server.model.entity.Record;
import pro.grape_server.model.entity.User;
import pro.grape_server.model.entity.enums.GrapeStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public GrapeInfoResult getInProgressGrapeInfo(Long userId) {
        Grape grape = grapeRepository.findByUserIdAndStatus(userId, GrapeStatus.IN_PROGRESS)
                .orElseThrow(() -> new BusinessException(ErrorCode.GRAPE_IN_PROGRESS_NOT_FOUND));

        int recordCount = (int) recordRepository.countByGrapeId(grape.getId());
        return GrapeInfoResult.from(grape, recordCount);
    }

    public boolean hasGrape(Long userId) {
        return grapeRepository.existsByUserId(userId);
    }

    public List<List<Integer>> getActivityGrid(Long userId, int weeks) {
        LocalDate today = LocalDate.now();

        // 오늘 기준 현재 주의 일요일 계산 (일=0, 월=1, ..., 토=6)
        int daysFromSunday = today.getDayOfWeek().getValue() % 7; // ISO: 월=1..일=7 → 일=0
        LocalDate currentWeekSunday = today.minusDays(daysFromSunday);

        LocalDate startDate = currentWeekSunday.minusWeeks(weeks - 1);
        LocalDate endDate = currentWeekSunday.plusDays(6);

        Set<LocalDate> recordDates = new HashSet<>(
                recordRepository.findRecordDatesByUserIdBetween(userId, startDate, endDate)
        );

        List<List<Integer>> grid = new ArrayList<>();
        LocalDate cursor = startDate;

        for (int w = 0; w < weeks; w++) {
            List<Integer> week = new ArrayList<>();
            for (int d = 0; d < 7; d++) {
                week.add(recordDates.contains(cursor) ? 1 : 0);
                cursor = cursor.plusDays(1);
            }
            grid.add(week);
        }

        return grid;
    }
}
