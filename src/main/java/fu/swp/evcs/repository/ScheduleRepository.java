package fu.swp.evcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fu.swp.evcs.entity.Schedule;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByGroupId(Long groupId);

    List<Schedule> findByUserId(Long userId);

    List<Schedule> findByGroupIdAndEndTimeAfterAndStartTimeBefore(
            Long groupId, LocalDateTime start, LocalDateTime end);

    List<Schedule> findByUserIdAndGroupIdAndStartTimeAfter(
            Long userId, Long groupId, LocalDateTime after);
}
