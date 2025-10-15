package swp302.topic6.evcoownership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO cho thông tin thành viên trong nhóm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Double ownershipPercentage;
    private String role; // admin, member
    private Date joinedAt;
    private String status; // active, pending, suspended
}
