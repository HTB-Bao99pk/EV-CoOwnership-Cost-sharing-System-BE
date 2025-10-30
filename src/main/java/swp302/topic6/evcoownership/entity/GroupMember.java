package swp302.topic6.evcoownership.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "group_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    // 🔹 Quan hệ với bảng CoOwnershipGroups
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private CoOwnershipGroup group;

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
