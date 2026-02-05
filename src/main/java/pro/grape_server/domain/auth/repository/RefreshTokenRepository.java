package pro.grape_server.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.grape_server.model.entity.RefreshToken;
import pro.grape_server.model.entity.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    void deleteByUser(User user);
}
