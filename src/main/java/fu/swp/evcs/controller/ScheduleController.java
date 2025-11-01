package fu.swp.evcs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.CheckInRequest;
import fu.swp.evcs.dto.CheckOutRequest;
import fu.swp.evcs.dto.ScheduleRequest;
import fu.swp.evcs.dto.ScheduleResponse;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.ScheduleService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse>> createBooking(@RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.createBooking(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getSchedules(@RequestParam(required = false) Long groupId) {
        if (groupId != null) {
            return ResponseEntity.ok(scheduleService.getSchedulesByGroup(groupId));
        }
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.cancelBooking(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> patchSchedule(@PathVariable Long id, @RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(scheduleService.updateStatus(id, status));
        }
        return ResponseEntity.ok(ApiResponse.success("Không có thay đổi nào", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getScheduleDetail(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleDetail(id));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<ApiResponse<ScheduleResponse>> checkIn(
            @PathVariable Long id,
            @RequestBody CheckInRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(scheduleService.checkIn(id, request, currentUser));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<ApiResponse<ScheduleResponse>> checkOut(
            @PathVariable Long id,
            @RequestBody CheckOutRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(scheduleService.checkOut(id, request, currentUser));
    }
}
