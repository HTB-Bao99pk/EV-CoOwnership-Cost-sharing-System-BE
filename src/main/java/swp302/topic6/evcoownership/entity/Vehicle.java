package swp302.topic6.evcoownership.entity;

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
@Table(name = "Vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicle_id;

    private String model;
    private String brand;


    @Column(name = "license_plate", unique = true)
    private String licensePlate;  // ✅ Thêm dòng này!

    private String location;
    private String status; // Active, Maintenance, etc.
    
    @Column(name = "purpose")
    private String purpose; // mục đích sử dụng phương tiện

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
