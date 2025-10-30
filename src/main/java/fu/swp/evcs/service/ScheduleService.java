package fu.swp.evcs.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.CheckInRequest;
import fu.swp.evcs.dto.CheckOutRequest;
import fu.swp.evcs.dto.ScheduleRequest;
import fu.swp.evcs.dto.ScheduleResponse;
import fu.swp.evcs.entity.Member;
import fu.swp.evcs.entity.Schedule;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.exception.BadRequestException;
import fu.swp.evcs.exception.UnauthorizedException;
import fu.swp.evcs.repository.MemberRepository;
import fu.swp.evcs.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    // 🧩 Helper
    private <T> ApiResponse<T> response(boolean success, String message, T data) {
        return new ApiResponse<>(success, message, data);
    }

    public ApiResponse<ScheduleResponse> createBooking(ScheduleRequest request) {
        var memberOpt = memberRepository
                .findByGroup_IdAndUser_Id(request.getGroupId(), request.getUserId());

        if (memberOpt.isEmpty())
            return response(false, "Bạn chưa tham gia nhóm này!", null);

        Member member = memberOpt.get();
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

        // Kiểm tra quota 3 tháng (90 ngày)
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Schedule> recent = scheduleRepository
                .findByUserIdAndGroupIdAndStartTimeAfter(request.getUserId(), request.getGroupId(), threeMonthsAgo);

        double usedDays = recent.stream()
                .mapToDouble(b -> Duration.between(b.getStartTime(), b.getEndTime()).toDays())
                .sum();

        double newDays = Duration.between(request.getStartTime(), request.getEndTime()).toDays();
        double maxDays = 0.9 * (percent / 100) * 90;

        if (usedDays + newDays > maxDays) {
            return response(false, "Bạn đã vượt giới hạn ngày cho phép (" + String.format("%.1f", maxDays) + " ngày trong 90 ngày)", null);
        }

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

    public ApiResponse<List<ScheduleResponse>> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        List<ScheduleResponse> responses = schedules.stream()
                .map(s -> mapToResponse(s, null))
                .toList();
        return response(true, "Danh sách lịch", responses);
    }

    public ApiResponse<List<ScheduleResponse>> getSchedulesByGroup(Long groupId) {
        List<Schedule> schedules = scheduleRepository.findByGroupId(groupId);
        List<ScheduleResponse> responses = schedules.stream()
                .map(s -> mapToResponse(s, null))
                .toList();
        return response(true, "Danh sách lịch theo nhóm", responses);
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

    public ApiResponse<ScheduleResponse> checkIn(Long scheduleId, CheckInRequest request, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy lịch!"));

        if (!schedule.getUserId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không phải người đặt lịch này!");
        }

        if (schedule.getCheckInTime() != null) {
            throw new BadRequestException("Lịch này đã check-in rồi!");
        }

        schedule.setCheckInTime(LocalDateTime.now());
        schedule.setBatteryLevelBefore(request.getBatteryLevelBefore());
        schedule.setStatus("in_progress");
        
        if (request.getNotes() != null) {
            schedule.setNotes(request.getNotes());
        }

        scheduleRepository.save(schedule);
        return response(true, "Check-in thành công!", mapToResponse(schedule, null));
    }

    public ApiResponse<ScheduleResponse> checkOut(Long scheduleId, CheckOutRequest request, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy lịch!"));

        if (!schedule.getUserId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không phải người đặt lịch này!");
        }

        if (schedule.getCheckInTime() == null) {
            throw new BadRequestException("Chưa check-in, không thể check-out!");
        }

        if (schedule.getCheckOutTime() != null) {
            throw new BadRequestException("Lịch này đã check-out rồi!");
        }

        schedule.setCheckOutTime(LocalDateTime.now());
        schedule.setBatteryLevelAfter(request.getBatteryLevelAfter());
        schedule.setVehicleCondition(request.getVehicleCondition());
        schedule.setStatus("completed");

        if (request.getNotes() != null) {
            String existingNotes = schedule.getNotes() != null ? schedule.getNotes() : "";
            schedule.setNotes(existingNotes + "\n[Check-out] " + request.getNotes());
        }

        BigDecimal penalty = BigDecimal.ZERO;
        StringBuilder violations = new StringBuilder();

        if (request.getBatteryLevelAfter() != null && request.getBatteryLevelAfter() < 90) {
            penalty = penalty.add(BigDecimal.valueOf(50000));
            violations.append("Pin dưới 90% (").append(request.getBatteryLevelAfter()).append("%). ");
        }

        LocalDateTime expectedEndTime = schedule.getEndTime();
        if (schedule.getCheckOutTime().isAfter(expectedEndTime)) {
            long lateMinutes = Duration.between(expectedEndTime, schedule.getCheckOutTime()).toMinutes();
            BigDecimal lateFee = BigDecimal.valueOf(lateMinutes * 1000);
            penalty = penalty.add(lateFee);
            violations.append("Trả xe trễ ").append(lateMinutes).append(" phút. ");
        }

        if (penalty.compareTo(BigDecimal.ZERO) > 0) {
            schedule.setPenaltyAmount(penalty);
            String existingNotes = schedule.getNotes() != null ? schedule.getNotes() : "";
            schedule.setNotes(existingNotes + "\n[Vi phạm] " + violations.toString() + 
                             "Phí phạt: " + penalty + " VND");
        }

        scheduleRepository.save(schedule);
        
        String message = penalty.compareTo(BigDecimal.ZERO) > 0 
                ? "Check-out thành công! Phạt: " + penalty + " VND. Lý do: " + violations.toString()
                : "Check-out thành công!";

        return response(true, message, mapToResponse(schedule, null));
    }

    private ScheduleResponse mapToResponse(Schedule s, Member m) {
        return ScheduleResponse.builder()
                .scheduleId(s.getId())
                .groupId(s.getGroupId())
                .userId(s.getUserId())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .status(s.getStatus())
                .ownershipPercentage(m != null ? m.getOwnershipPercentage() : 0)
                .userName(m != null ? m.getUser().getFullName() : null)
                .groupName(m != null ? m.getGroup().getName() : null)

                .build();
    }
}
