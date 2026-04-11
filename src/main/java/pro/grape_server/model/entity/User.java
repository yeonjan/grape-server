package pro.grape_server.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pro.grape_server.model.entity.common.BaseEntity;
import pro.grape_server.model.entity.enums.Provider;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_provider_provider_user_id",
                        columnNames = {"provider", "providerUserId"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    @Column(nullable = false, length = 100)
    private String providerUserId;

    @Column(length = 50)
    private String nickname;

    private LocalDateTime lastLoginAt;

    @Builder(access = AccessLevel.PRIVATE)
    private User(Provider provider, String providerUserId) {
        this.provider = provider;
        this.providerUserId = providerUserId;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public static User create(Provider provider, String providerUserId) {
        return User.builder()
                .provider(provider)
                .providerUserId(providerUserId)
                .build();
    }

    public static User createGuest(String deviceHash) {
        return User.builder()
                .provider(Provider.GUEST)
                .providerUserId(deviceHash)
                .build();
    }

    public boolean isGuest() {
        return this.provider == Provider.GUEST;
    }

    public void convertToSocialAccount(Provider provider, String providerUserId) {
        this.provider = provider;
        this.providerUserId = providerUserId;
    }
}
