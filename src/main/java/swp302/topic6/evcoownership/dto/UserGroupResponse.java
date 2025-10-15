package swp302.topic6.evcoownership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO cho danh sách nhóm của user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupResponse {
    private Long id;
    private String groupName;
    private String description;
    private String vehicleName;
    private String vehicleModel;
    private Integer currentMembers;
    private Integer maxMembers;
    private Double myOwnershipPercentage;
    private Double totalOwnershipPercentage;
    private Double estimatedValue;
    private Double monthlyFee;
    private String status; // active, pending, recruiting, full, closed
    private String role; // admin, member
    private Date createdAt;
}
