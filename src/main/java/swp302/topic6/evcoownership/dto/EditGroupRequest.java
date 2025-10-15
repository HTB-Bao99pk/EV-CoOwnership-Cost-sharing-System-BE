package swp302.topic6.evcoownership.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho request chỉnh sửa nhóm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditGroupRequest {
    private String groupName;
    private String description;
    private Integer maxMembers;
    private BigDecimal minOwnershipPercentage;
}
