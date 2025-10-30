package fu.swp.evcs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckOutRequest {
    
    private Integer batteryLevelAfter;
    
    private String vehicleCondition;
    
    private String notes;
}
