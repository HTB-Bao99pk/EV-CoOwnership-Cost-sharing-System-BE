package fu.swp.evcs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Bảng CoOwnershipGroups
 * Lưu thông tin nhóm chia sẻ xe
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

    // 🔹 Xe thuộc nhóm này
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // 🔹 Người tạo nhóm (thường là chủ xe)
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // 🔹 Admin duyệt nhóm
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    // 🔹 recruiting, active, closed
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "recruiting";

    @Column(name = "estimated_value")
    private Double estimatedValue;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    @Builder.Default
    private Date createdAt = new Date();

    // 🔹 pending, approved, rejected
    @Column(name = "approval_status", length = 20)
    @Builder.Default
    private String approvalStatus = "pending";

    @Column(name = "reject_reason", length = 255)
    private String rejectReason;
    // Giới hạn số thành viên tối đa (mặc định 5)
    @Column(name = "max_members")
    @Builder.Default
    private Integer maxMembers = 5;

    // Tỷ lệ sở hữu tối thiểu (%) để tham gia nhóm (mặc định 10.00)
    @Column(name = "min_ownership_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal minOwnershipPercentage = new BigDecimal("10.00");
}

