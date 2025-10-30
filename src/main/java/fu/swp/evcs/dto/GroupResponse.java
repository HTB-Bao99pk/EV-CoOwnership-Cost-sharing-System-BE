package fu.swp.evcs.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponse {
    
    private Long id;
    private String name;
    private String description;
    private String status;
    private String approvalStatus;
    private Double estimatedValue;
    private Integer maxMembers;
    private BigDecimal minOwnershipPercentage;
    private BigDecimal totalOwnershipPercentage;
    private Boolean isLocked;
    private String contractUrl;
    private BigDecimal balance;
    private Date createdAt;
    
    private Long vehicleId;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehicleLicensePlate;
    
    private Long createdById;
    private String createdByName;
    
    private Long approvedById;
    private String approvedByName;
    
    private String rejectReason;
}
