package fu.swp.evcs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * CoOwnershipGroups table - Stores information about car sharing groups
 */
@Entity
@Table(name = "groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Vehicle belonging to this group
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // Group creator (usually the vehicle owner)
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Admin who approves the group
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "recruiting";

    @Column(name = "estimated_value")
    private Double estimatedValue;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    @Builder.Default
    private Date createdAt = new Date();

    @Column(name = "approval_status", length = 20)
    @Builder.Default
    private String approvalStatus = "pending";

    @Column(name = "reject_reason", length = 255)
    private String rejectReason;
    // Maximum number of members limit (default 5)
    @Column(name = "max_members")
    @Builder.Default
    private Integer maxMembers = 5;

    // Minimum ownership percentage (%) to join the group (default 10.00)
    @Column(name = "min_ownership_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal minOwnershipPercentage = new BigDecimal("10.00");
}
