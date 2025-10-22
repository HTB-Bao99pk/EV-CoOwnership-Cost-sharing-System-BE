package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.dto.*;
import swp302.topic6.evcoownership.entity.*;
import swp302.topic6.evcoownership.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupMemberRepository groupMemberRepository;

    // 🧩 Helper
    private <T> ApiResponse<T> response(boolean success, String message, T data) {
        return new ApiResponse<>(success, message, data);
    }

    public ApiResponse<ScheduleResponse> createBooking(ScheduleRequest request) {
        var memberOpt = groupMemberRepository
                .findByGroup_GroupIdAndUser_UserId(request.getGroupId(), request.getUserId());

        if (memberOpt.isEmpty())
            return response(false, "Bạn chưa tham gia nhóm này!", null);

        GroupMember member = memberOpt.get();
        if (!"approved".equalsIgnoreCase(member.getJoinStatus()))
            return response(false, "Tài khoản của bạn chưa được duyệt để đặt lịch!", null);

        double percent = member.getOwnershipPercentage();

        // Kiểm tra trùng hoặc cách 1h
        List<Schedule> overlaps = scheduleRepository
                .findByGroupIdAndEndTimeAfterAndStartTimeBefore(
                        request.getGroupId(),
                        request.getStartTime().minusHours(1),
                        request.getEndTime().plusHours(1)
                );
        if (!overlaps.isEmpty())
            return response(false, "Xe đã được đặt hoặc chưa cách đủ 1 tiếng!", null);

        // Kiểm tra quota 3 tháng
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Schedule> recent = scheduleRepository
                .findByUserIdAndGroupIdAndStartTimeAfter(request.getUserId(), request.getGroupId(), threeMonthsAgo);

        double usedHours = recent.stream()
                .mapToDouble(b -> Duration.between(b.getStartTime(), b.getEndTime()).toHours())
                .sum();

        double newHours = Duration.between(request.getStartTime(), request.getEndTime()).toHours();
        double maxHours = 2160 * (percent / 100);

        if (usedHours + newHours > maxHours)
            return response(false, "Bạn đã vượt giới hạn giờ cho phép (" + maxHours + "h)", null);

        // Lưu
        Schedule schedule = new Schedule();
        schedule.setGroupId(request.getGroupId());
        schedule.setUserId(request.getUserId());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setStatus("scheduled");
        scheduleRepository.save(schedule);

        ScheduleResponse res = mapToResponse(schedule, member);
        return response(true, "Đặt lịch thành công!", res);
    }

    public ApiResponse<List<ScheduleResponse>> getSchedulesByGroup(Long groupId) {
        List<Schedule> schedules = scheduleRepository.findByGroupId(groupId);
        List<ScheduleResponse> responses = schedules.stream()
                .map(s -> mapToResponse(s, null))
                .toList();
        return response(true, "Danh sách lịch", responses);
    }

    public ApiResponse<ScheduleResponse> cancelBooking(Long scheduleId) {
        Optional<Schedule> opt = scheduleRepository.findById(scheduleId);
        if (opt.isEmpty())
            return response(false, "Không tìm thấy lịch!", null);

        Schedule schedule = opt.get();
        schedule.setStatus("cancelled");
        scheduleRepository.save(schedule);

        return response(true, "Hủy lịch thành công!", mapToResponse(schedule, null));
    }

    public ApiResponse<ScheduleResponse> updateStatus(Long scheduleId, String status) {
        Optional<Schedule> opt = scheduleRepository.findById(scheduleId);
        if (opt.isEmpty())
            return response(false, "Không tìm thấy lịch!", null);

        Schedule schedule = opt.get();
        schedule.setStatus(status);
        scheduleRepository.save(schedule);

        return response(true, "Cập nhật trạng thái thành công!", mapToResponse(schedule, null));
    }

    public ApiResponse<ScheduleResponse> getScheduleDetail(Long scheduleId) {
        Optional<Schedule> opt = scheduleRepository.findById(scheduleId);
        return opt.map(s -> response(true, "Chi tiết lịch", mapToResponse(s, null)))
                .orElseGet(() -> response(false, "Không tìm thấy lịch!", null));
    }

    private ScheduleResponse mapToResponse(Schedule s, GroupMember m) {
        return ScheduleResponse.builder()
                .scheduleId(s.getScheduleId())
                .groupId(s.getGroupId())
                .userId(s.getUserId())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .status(s.getStatus())
                .ownershipPercentage(m != null ? m.getOwnershipPercentage() : 0)
                .userName(m != null ? m.getUser().getFullName() : null)
                .groupName(m != null ? m.getGroup().getGroupName() : null)

                .build();
    }
}
