package swp302.topic6.evcoownership.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "CoOwnershipGroups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoOwnershipGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long group_id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User created_by;

    private String group_name;
    private String description;
    private String status; // recruiting | active | closed
    private Double estimated_value;
    private Date created_at;
    private String approval_status; // pending | approved | rejected
    private String reject_reason;
}
