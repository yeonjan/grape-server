package pro.grape_server.domain.grape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.grape_server.model.entity.Grape;
import pro.grape_server.model.entity.User;
import pro.grape_server.model.entity.enums.GrapeStatus;

import java.util.List;
import java.util.Optional;

public interface GrapeRepository extends JpaRepository<Grape, Long> {
    boolean existsByUserId(Long userId);

    boolean existsByUserIdAndStatus(Long userId, GrapeStatus status);

    Optional<Grape> findByUserIdAndStatus(Long userId, GrapeStatus status);

    Long user(User user);
}
