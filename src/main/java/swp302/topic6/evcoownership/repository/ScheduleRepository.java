package swp302.topic6.evcoownership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp302.topic6.evcoownership.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByGroupId(Long groupId);

    // Kiểm tra trùng lịch trong cùng group
    List<Schedule> findByGroupIdAndEndTimeAfterAndStartTimeBefore(
            Long groupId, LocalDateTime start, LocalDateTime end
    );

    // Lấy lịch của user trong 3 tháng gần nhất
    List<Schedule> findByUserIdAndGroupIdAndStartTimeAfter(Long userId, Long groupId, LocalDateTime after);
}
