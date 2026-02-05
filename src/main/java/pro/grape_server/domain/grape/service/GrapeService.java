package pro.grape_server.domain.grape.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.grape_server.domain.auth.repository.UserRepository;
import pro.grape_server.domain.grape.repository.RecordRepository;
import pro.grape_server.domain.grape.repository.GrapeRepository;
import pro.grape_server.domain.grape.service.dto.GrapeOverviewResult;
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
            throw new IllegalStateException("이미 진행 중인 포도가 있습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Grape grape = Grape.create(user, title, targetCount, reward);
        grapeRepository.save(grape);
        return grape.getId();
    }

    public GrapeOverviewResult getOverview(Long userId, Long grapeId) {
        Grape grape = grapeRepository.findById(grapeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포도입니다."));

        if (!grape.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 포도만 조회할 수 있습니다.");
        }

        List<Record> records = recordRepository.findAllByGrapeId(grape.getId());
        return GrapeOverviewResult.from(grape, records);
    }

    public GrapeOverviewResult getInProgressGrapeOverView(Long userId) {
        Grape grape = grapeRepository.findByUserIdAndStatus(userId, GrapeStatus.IN_PROGRESS)
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 포도가 없습니다."));

        List<Record> records = recordRepository.findAllByGrapeId(grape.getId());
        return GrapeOverviewResult.from(grape, records);
    }


    public boolean hasGrape(Long userId) {
        return grapeRepository.existsByUserId(userId);
    }
}
