package swp302.topic6.evcoownership.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO cho request body khi từ chối yêu cầu tham gia
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelRequestBody {
    private String reason;
}
