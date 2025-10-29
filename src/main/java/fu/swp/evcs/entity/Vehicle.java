package fu.swp.evcs.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private String model;
    private String brand;


    @Column(name = "license_plate", unique = true)
    private String licensePlate;

    private String location;
    private String status; // Active, Maintenance, etc.

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
