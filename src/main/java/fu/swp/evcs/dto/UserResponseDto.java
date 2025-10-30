package fu.swp.evcs.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    
    private Long id;
    private String fullName;
    private String email;
    private String cccd;
    private String driverLicense;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    
    private String role;
    private String verificationStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String location;
    private String cccdFrontUrl;
    private String cccdBackUrl;
    private String driverLicenseUrl;
}
