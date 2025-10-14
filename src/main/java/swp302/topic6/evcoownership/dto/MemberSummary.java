package swp302.topic6.evcoownership.dto;

import java.util.Date;

import lombok.Data;

@Data
public class MemberSummary {
    private Long memberId;
    private Long userId;
    private String fullName;
    private Double ownershipPercentage;
    private String joinStatus;
    private Date joinDate;
}
