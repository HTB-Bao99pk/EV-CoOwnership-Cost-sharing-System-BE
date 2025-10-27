package fu.swp.evcs.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponse {
    private Long scheduleId;
    private Long groupId;
    private Long userId;

    private String userName;
    private String groupName;
    private double ownershipPercentage;
    private String userColor; // FE có thể dùng để tô màu lịch

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // scheduled, cancelled, in_use, completed
}
