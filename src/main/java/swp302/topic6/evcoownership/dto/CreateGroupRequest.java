package swp302.topic6.evcoownership.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateGroupRequest {
    private Long vehicleId;
    private String groupName;
    private String description;
    private Double estimatedValue;
    private Integer maxMembers;
    private Double minOwnershipPercentage; // percent, e.g., 5.0 for

}