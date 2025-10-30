package swp302.topic6.evcoownership.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "status")
    private String status = "scheduled"; // scheduled / cancelled / cancelled_late / completed

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime; // thời gian thực tế trả xe

    @Column(name = "battery_level")
    private Double batteryLevel; // % pin khi trả xe

    @Column(name = "penalty_amount")
    private Double penaltyAmount = 0.0; // tổng tiền phạt (pin yếu, trễ, hủy gấp)
}
