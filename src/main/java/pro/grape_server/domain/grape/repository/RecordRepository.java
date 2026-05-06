package pro.grape_server.domain.grape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.grape_server.model.entity.Record;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    boolean existsByGrapeIdAndRecordDate(Long grapeId, LocalDate recordDate);

    List<Record> findAllByGrapeIdOrderByRecordDateAsc(Long grapeId);

    @Query("SELECT r.recordDate FROM Record r WHERE r.user.id = :userId AND r.recordDate BETWEEN :start AND :end")
    List<LocalDate> findRecordDatesByUserIdBetween(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
