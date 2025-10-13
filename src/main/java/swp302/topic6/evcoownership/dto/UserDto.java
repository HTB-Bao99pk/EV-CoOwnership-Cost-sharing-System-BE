package swp302.topic6.evcoownership.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long userId;
    private String fullName;
    private String email;
    private String cccd;
    private String driverLicense;
    private LocalDate birthday;
    private String role;
    private String verificationStatus;
    private LocalDateTime createdAt;
    private String location;
    private String cccdFrontUrl;
    private String cccdBackUrl;
    private String driverLicenseUrl;
}
