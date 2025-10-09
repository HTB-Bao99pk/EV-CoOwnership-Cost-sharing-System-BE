package swp302.topic6.evcoownership.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "cccd")
    private String cccd;

    @Column(name = "driver_license")
    private String driverLicense;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "role")
    private String role;

    @Column(name = "verification_status")
    private String verificationStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "location")
    private String location;

    // 🧩 Các đường dẫn ảnh upload
    @Column(name = "cccd_front_url")
    private String cccdFrontUrl;

    @Column(name = "cccd_back_url")
    private String cccdBackUrl;

    @Column(name = "driver_license_url")
    private String driverLicenseUrl;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
