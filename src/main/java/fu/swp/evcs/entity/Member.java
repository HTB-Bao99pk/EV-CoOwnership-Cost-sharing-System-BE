package fu.swp.evcs.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 🔹 Quan hệ với bảng CoOwnershipGroups
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // 🔹 Quan hệ với bảng Users
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ownership_percentage")
    private Double ownershipPercentage;

    @Column(name = "join_status")
    private String joinStatus; // pending, approved, rejected

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "join_date")
    private Date joinDate;
}
