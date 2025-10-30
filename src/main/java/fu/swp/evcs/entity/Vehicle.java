package fu.swp.evcs.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String model;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String brand;

    @Column(name = "license_plate", unique = true)
    private String licensePlate;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String location;
    
    @Column(name = "status")
    @Builder.Default
    private String status = "pending_approval";

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "registration_info", length = 500, columnDefinition = "NVARCHAR(500)")
    private String registrationInfo;

    @Column(name = "battery_capacity")
    private Integer batteryCapacity;

    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;

    @Column(name = "image_url_1")
    private String imageUrl1;

    @Column(name = "image_url_2")
    private String imageUrl2;

    @Column(name = "image_url_3")
    private String imageUrl3;

    @Column(name = "verification_status")
    @Builder.Default
    private String verificationStatus = "pending";

    @ManyToOne
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "reject_reason", columnDefinition = "NVARCHAR(500)")
    private String rejectReason;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
