package swp302.topic6.evcoownership.dto;

import lombok.Data;

@Data
public class JoinGroupRequest {
    // ⭐️ TỐI ƯU: Xoá userId. userId PHẢI được lấy từ session,
    // không bao giờ được tin tưởng userId do client gửi lên.
    // private Long userId;

    private Double requestedPercentage; // percent, e.g., 10.0 for 10%
}