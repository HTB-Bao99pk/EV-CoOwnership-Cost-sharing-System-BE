package swp302.topic6.evcoownership.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
