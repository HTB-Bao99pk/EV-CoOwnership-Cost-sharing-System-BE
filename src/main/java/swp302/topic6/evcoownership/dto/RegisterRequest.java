package swp302.topic6.evcoownership.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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

    // Ảnh upload thực tế (file)
    private MultipartFile cccdFront;
    private MultipartFile cccdBack;
    private MultipartFile driverLicenseImg;
}
