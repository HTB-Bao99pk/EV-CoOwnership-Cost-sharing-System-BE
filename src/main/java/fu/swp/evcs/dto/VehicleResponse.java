package fu.swp.evcs.dto;

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
    private String purpose;
    private Long ownerId;
    private String ownerName; // Tên của owner để hiển thị
}
