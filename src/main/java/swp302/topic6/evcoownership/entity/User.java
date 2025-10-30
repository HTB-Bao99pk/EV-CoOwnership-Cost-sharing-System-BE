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

    @Column(name = "cccd", nullable = false)
    private String cccd;

    @Column(name = "driver_license", nullable = false)
    private String driverLicense;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "verification_status", nullable = false)
    private String verificationStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "location", nullable = false)
    private String location;

    // 🧩 Các đường dẫn ảnh upload (có thể cho phép null nếu người dùng chưa upload)
    @Column(name = "cccd_front_url", nullable = false)
    private String cccdFrontUrl;

    @Column(name = "cccd_back_url", nullable = false)
    private String cccdBackUrl;

    @Column(name = "driver_license_url", nullable = false)
    private String driverLicenseUrl;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
