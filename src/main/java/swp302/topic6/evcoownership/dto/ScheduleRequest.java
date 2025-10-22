package swp302.topic6.evcoownership.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScheduleRequest {
    private Long groupId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose; // ghi chú sử dụng xe
}
