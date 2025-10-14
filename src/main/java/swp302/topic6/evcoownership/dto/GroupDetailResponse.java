package swp302.topic6.evcoownership.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class GroupDetailResponse {
    private Long groupId;
    private Long vehicleId;
    private Long createdByUserId;
    private String groupName;
    private String description;
    private String status;
    private String approvalStatus;
    private Date createdAt;
    private Integer maxMembers;
    private BigDecimal minOwnershipPercentage;
    private Integer memberCount; // <-- số thành viên active hiện tại
}
