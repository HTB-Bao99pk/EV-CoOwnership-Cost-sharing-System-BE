package fu.swp.evcs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewMemberRequest {
    
    private String action;
    
    private Double counterOfferPercentage;
    
    private String reason;
}
