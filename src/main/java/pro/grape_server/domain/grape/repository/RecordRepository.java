package pro.grape_server.domain.grape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.grape_server.model.entity.Record;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    boolean existsByGrapeIdAndRecordDate(Long grapeId, LocalDate recordDate);

    List<Record> findAllByGrapeId(Long grapeId);
}
