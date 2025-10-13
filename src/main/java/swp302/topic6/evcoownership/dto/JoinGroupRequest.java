package swp302.topic6.evcoownership.dto;

import lombok.Data;

@Data
public class JoinGroupRequest {
    private Long userId;
    private Double requestedPercentage; // percent, e.g., 10.0 for 10%
}
