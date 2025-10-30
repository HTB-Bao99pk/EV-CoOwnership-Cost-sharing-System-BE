package fu.swp.evcs.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    private Long vehicleId;
    private String model;
    private String brand;
    private String licensePlate;
    private String location;
    private String status;
    private String registrationInfo;
    private Integer batteryCapacity;
    private Integer yearOfManufacture;
    private String imageUrl1;
    private String imageUrl2;
    private String imageUrl3;
    private String verificationStatus;
    private String rejectReason;
    private LocalDateTime verifiedAt;
    private Long ownerId;
    private String ownerName;
    private String verifiedByName;
}
