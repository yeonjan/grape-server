package pro.grape_server.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.grape_server.model.entity.User;
import pro.grape_server.model.entity.enums.Provider;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndProviderUserId(Provider provider, String providerUserId);
}
