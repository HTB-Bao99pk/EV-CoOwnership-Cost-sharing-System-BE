package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.dto.ApiResponse;
import swp302.topic6.evcoownership.dto.ScheduleRequest;
import swp302.topic6.evcoownership.dto.ScheduleResponse;
import swp302.topic6.evcoownership.entity.GroupMember;
import swp302.topic6.evcoownership.entity.Schedule;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.repository.GroupMemberRepository;
import swp302.topic6.evcoownership.repository.ScheduleRepository;
import swp302.topic6.evcoownership.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    // =============================
    // 1️⃣ Tạo mới lịch đặt xe
    // =============================
    public ApiResponse<ScheduleResponse> createSchedule(ScheduleRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        Optional<GroupMember> groupMemberOpt = groupMemberRepository.findById(request.getGroupId());

        if (userOpt.isEmpty() || groupMemberOpt.isEmpty()) {
            return response(false, "Không tìm thấy người dùng hoặc nhóm!", null);
        }

        Schedule schedule = Schedule.builder()
                .groupId(request.getGroupId())
                .userId(request.getUserId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status("scheduled")
                .penaltyAmount(0.0) // reset penalty
                .build();

        scheduleRepository.save(schedule);
        return response(true, "Tạo lịch thành công!", mapToResponse(schedule));
    }

    // =============================
    // 2️⃣ Hủy lịch (phạt nếu <24h)
    // =============================
    public ApiResponse<ScheduleResponse> cancelSchedule(Long scheduleId) {
        Optional<Schedule> opt = scheduleRepository.findById(scheduleId);
        if (opt.isEmpty()) return response(false, "Không tìm thấy lịch!", null);

        Schedule schedule = opt.get();
        if (!"scheduled".equalsIgnoreCase(schedule.getStatus())) {
            return response(false, "Chỉ có thể hủy lịch đang ở trạng thái 'scheduled'!", null);
        }

        Duration diff = Duration.between(LocalDateTime.now(), schedule.getStartTime());
        double penalty = 0;

        if (diff.toHours() < 24) {
            penalty = 10_000; // ví dụ phạt nhỏ, bạn có thể tăng tuỳ ý
            schedule.setStatus("cancelled_late");
        } else {
            schedule.setStatus("cancelled");
        }

        schedule.setPenaltyAmount(penalty);
        scheduleRepository.save(schedule);

        String msg = (penalty > 0)
                ? "Hủy lịch gấp (<24h) - phạt " + String.format("%,.0f VNĐ", penalty)
                : "Hủy lịch thành công!";
        return response(true, msg, mapToResponse(schedule));
    }

    // =============================
    // 3️⃣ Trả xe (tính phạt pin yếu & trễ)
    // =============================
    public ApiResponse<ScheduleResponse> returnVehicle(Long scheduleId, double batteryLevel, LocalDateTime actualEndTime) {
        Optional<Schedule> opt = scheduleRepository.findById(scheduleId);
        if (opt.isEmpty()) return response(false, "Không tìm thấy lịch!", null);

        Schedule schedule = opt.get();
        if (!List.of("scheduled", "in_use").contains(schedule.getStatus().toLowerCase())) {
            return response(false, "Lịch không hợp lệ để trả xe!", null);
        }

        double fine = 0;

        // ⚡ 1. Phạt pin yếu
        if (batteryLevel < 90) {
            fine += 150_000;
            if (batteryLevel < 75) {
                fine += (75 - batteryLevel) * 5_000;
            }
        }

        // ⏰ 2. Phạt trả muộn
        if (actualEndTime.isAfter(schedule.getEndTime())) {
            Duration lateDuration = Duration.between(schedule.getEndTime(), actualEndTime);
            long lateHours = lateDuration.toHours();
            long blocks = lateHours / 3 + (lateHours % 3 > 0 ? 1 : 0);
            fine += blocks * 1_000_000;

            // cập nhật thời gian kết thúc mới
            schedule.setEndTime(actualEndTime);
        }

        // ✅ Lưu thông tin
        schedule.setBatteryLevel(batteryLevel);
        schedule.setActualEndTime(actualEndTime);
        schedule.setPenaltyAmount(fine);
        schedule.setStatus("completed");
        scheduleRepository.save(schedule);

        String message = "Trả xe thành công!";
        if (fine > 0) message += " Tổng tiền phạt: " + String.format("%,.0f VNĐ", fine);

        return response(true, message, mapToResponse(schedule));
    }

    // =============================
    // 4️⃣ Tổng kết nhóm
    // =============================
    public ApiResponse<Map<String, Object>> getGroupSummary(Long groupId) {
        List<Schedule> list = scheduleRepository.findByGroupId(groupId);

        long totalTrips = list.size();
        long completed = list.stream().filter(s -> "completed".equalsIgnoreCase(s.getStatus())).count();
        long cancelled = list.stream().filter(s -> s.getStatus().startsWith("cancelled")).count();
        double totalPenalty = list.stream()
                .filter(s -> s.getPenaltyAmount() != null)
                .mapToDouble(Schedule::getPenaltyAmount)
                .sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalTrips", totalTrips);
        summary.put("completed", completed);
        summary.put("cancelled", cancelled);
        summary.put("totalPenalty", totalPenalty);

        return response(true, "Tổng kết nhóm", summary);
    }

    // =============================
    // 🔁 Tiện ích chung
    // =============================
    private ScheduleResponse mapToResponse(Schedule s) {
        return ScheduleResponse.builder()
                .scheduleId(s.getScheduleId())
                .groupId(s.getGroupId())
                .userId(s.getUserId())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .status(s.getStatus())
                .build();
    }

    private <T> ApiResponse<T> response(boolean success, String msg, T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(success);
        res.setMessage(msg);
        res.setData(data);
        return res;
    }
}
