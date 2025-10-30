package fu.swp.evcs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private String verificationStatus;
    private String sessionId;
}
