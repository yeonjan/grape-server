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

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String email;

    private LocalDateTime lastLoginAt;

    @Builder(access = AccessLevel.PRIVATE)
    private User(Provider provider, String providerUserId, String name, String email) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.name = name;
        this.email = email;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public static User create(Provider provider, String providerUserId, String name, String email) {
        return User.builder()
                .provider(provider)
                .providerUserId(providerUserId)
                .name(name)
                .email(email)
                .build();
    }

    public static User createGuest(String deviceHash) {
        return User.builder()
                .provider(Provider.GUEST)
                .providerUserId(deviceHash)
                .name("Guest")
                .build();
    }

    public boolean isGuest() {
        return this.provider == Provider.GUEST;
    }

    public void convertToSocialAccount(Provider provider, String providerUserId, String name, String email) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.name = name;
        this.email = email;
    }
}
