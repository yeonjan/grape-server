package pro.grape_server.model.entity;

import jakarta.persistence.*;
import lombok.*;
import pro.grape_server.model.entity.enums.GrapeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "grapes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class Grape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private int targetCount;

    @Column(length = 200)
    private String reward;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GrapeStatus status;


    public static Grape create(User user, String title, int targetCount, String reward) {
        validateTargetCount(targetCount);
        return Grape.builder().user(user).title(title).targetCount(targetCount).reward(reward).status(GrapeStatus.IN_PROGRESS).build();
    }

    private static void validateTargetCount(int targetCount) {
        if (targetCount != 30 && targetCount != 50 && targetCount != 100) {
            throw new IllegalArgumentException("허용되지 않은 targetCount");
        }
    }

}