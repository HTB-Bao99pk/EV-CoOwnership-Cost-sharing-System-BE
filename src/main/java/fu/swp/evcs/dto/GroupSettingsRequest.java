package fu.swp.evcs.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupSettingsRequest {

    private Integer maxMembers;                // Số lượng thành viên tối đa
    private BigDecimal minOwnershipPercentage; // Tỷ lệ sở hữu tối thiểu (vd: 10.00)
}
