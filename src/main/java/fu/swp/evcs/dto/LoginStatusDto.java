package fu.swp.evcs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ✅ LoginStatusDto - DTO trả về trạng thái login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginStatusDto {
    
    private Boolean isAuthenticated;
    
    private String sessionId;
    
    private Long userId;
    
    private String email;
    
    private String role;
}
