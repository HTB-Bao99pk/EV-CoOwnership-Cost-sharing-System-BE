package swp302.topic6.evcoownership.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String cccd;
    private String driverLicense;
    private LocalDate birthday;
    private String location;
    private String cccdFrontBase64;
    private String cccdBackBase64;
    private String driverLicenseBase64;
}
