package fu.swp.evcs.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GroupUpdateRequest {
    private String name;
    private String description;
    private Double estimatedValue;
    private Integer maxMembers;
    private BigDecimal minOwnershipPercentage;
    // Thêm các trường có thể cập nhật khác của Group vào đây
}