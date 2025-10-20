package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.entity.GroupMember;
import swp302.topic6.evcoownership.entity.Schedule;
import swp302.topic6.evcoownership.repository.GroupMemberRepository;
import swp302.topic6.evcoownership.repository.ScheduleRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupMemberRepository groupMemberRepository;

    public Map<String, Object> createBooking(Schedule schedule) {
        Map<String, Object> response = new HashMap<>();

        // 1️⃣ Kiểm tra thành viên
        var memberOpt = groupMemberRepository
                .findByGroup_GroupIdAndUser_UserId(schedule.getGroupId(), schedule.getUserId());

        if (memberOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Bạn chưa tham gia nhóm này!");
            return response;
        }

        GroupMember member = memberOpt.get();

        if (!"approved".equalsIgnoreCase(member.getJoinStatus())) {
            response.put("success", false);
            response.put("message", "Tài khoản của bạn chưa được duyệt để đặt lịch!");
            return response;
        }

        double percent = member.getOwnershipPercentage();

        // 2️⃣ Kiểm tra trùng lịch hoặc cách 1 tiếng
        List<Schedule> overlaps = scheduleRepository
                .findByGroupIdAndEndTimeAfterAndStartTimeBefore(
                        schedule.getGroupId(),
                        schedule.getStartTime().minusHours(1),
                        schedule.getEndTime().plusHours(1)
                );

        if (!overlaps.isEmpty()) {
            response.put("success", false);
            response.put("message", "Xe đã được đặt trong khung giờ này hoặc chưa đủ 1 tiếng cách nhau!");
            return response;
        }

        // 3️⃣ Kiểm tra tổng số giờ trong 3 tháng gần nhất
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Schedule> recentBookings = scheduleRepository
                .findByUserIdAndGroupIdAndStartTimeAfter(schedule.getUserId(), schedule.getGroupId(), threeMonthsAgo);

        double usedHours = recentBookings.stream()
                .mapToDouble(b -> Duration.between(b.getStartTime(), b.getEndTime()).toHours())
                .sum();

        double newHours = Duration.between(schedule.getStartTime(), schedule.getEndTime()).toHours();
        double totalHours = usedHours + newHours;

        double maxHours = 2160 * (percent / 100); // 3 tháng = 2160h

        if (totalHours > maxHours) {
            response.put("success", false);
            response.put("message", "Bạn đã dùng hết số giờ cho phép trong 3 tháng (" + maxHours + "h)");
            return response;
        }

        // 4️⃣ Lưu lịch
        scheduleRepository.save(schedule);
        response.put("success", true);
        response.put("message", "Đặt lịch thành công!");
        response.put("data", schedule);
        return response;
    }

    public List<Schedule> getSchedulesByGroup(Long groupId) {
        return scheduleRepository.findByGroupId(groupId);
    }
}
