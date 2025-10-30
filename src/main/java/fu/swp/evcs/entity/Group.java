package fu.swp.evcs.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "name", length = 100, columnDefinition = "NVARCHAR(100)")
    private String name;

    @Column(name = "description", length = 255, columnDefinition = "NVARCHAR(255)")
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

    @Column(name = "reject_reason", length = 255, columnDefinition = "NVARCHAR(255)")
    private String rejectReason;
    // Maximum number of members limit (default 5)
    @Column(name = "max_members")
    @Builder.Default
    private Integer maxMembers = 5;

    @Column(name = "min_ownership_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal minOwnershipPercentage = new BigDecimal("15.00");

    @Column(name = "total_ownership_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal totalOwnershipPercentage = new BigDecimal("0.00");

    @Column(name = "is_locked")
    @Builder.Default
    private Boolean isLocked = false;

    @Column(name = "contract_url", length = 500)
    private String contractUrl;

    @Column(name = "balance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = new BigDecimal("0.00");
}
