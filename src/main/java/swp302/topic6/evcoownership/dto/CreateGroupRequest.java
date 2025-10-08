package swp302.topic6.evcoownership.dto;

import lombok.Data;

@Data
public class CreateGroupRequest {
    private Long vehicleId;
    private String groupName;
    private String description;
    private Double estimatedValue;
}
