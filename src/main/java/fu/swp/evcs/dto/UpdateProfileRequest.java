package fu.swp.evcs.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    private String fullName;
    private String location;
    private LocalDate birthday;
    private String driverLicense;
}
