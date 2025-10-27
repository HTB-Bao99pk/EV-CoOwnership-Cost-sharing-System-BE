package fu.swp.evcs.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Entity - implement UserDetails để tích hợp trực tiếp với Spring Security
 * Không cần wrapper class, đơn giản và gọn hơn
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    // ============ UserDetails Implementation ============
    
    /**
     * Trả về quyền của user - ROLE_ADMIN hoặc ROLE_USER
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    /**
     * Trả về password đã hash
     */
    @Override
    @JsonIgnore
    public String getPassword() {
        return passwordHash;
    }

    /**
     * Username cho Spring Security = email
     */
    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    /**
     * Account không bao giờ expired
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Account không bao giờ bị lock
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Credentials không bao giờ expired
     */
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Account chỉ enabled khi đã được verify bởi admin
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return "verified".equalsIgnoreCase(verificationStatus);
    }
}
