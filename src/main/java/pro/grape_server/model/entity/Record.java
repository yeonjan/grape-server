package pro.grape_server.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "record",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_grape_day_per_day",
                        columnNames = {"grape_id", "record_date"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grape_id", nullable = false)
    private Grape grape;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(length = 500)
    private String memo;

    public static Record create(Grape grape, User user, String memo, LocalDate recordDate) {
        return Record.builder().grape(grape).user(user).memo(memo).recordDate(recordDate).build();
    }


}