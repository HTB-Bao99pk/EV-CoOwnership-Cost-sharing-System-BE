package swp302.topic6.evcoownership.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.dto.*;
import swp302.topic6.evcoownership.service.ScheduleService;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ScheduleResponse>> createBooking(@RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.createBooking(request));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<?>> getSchedulesByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByGroup(groupId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.cancelBooking(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(scheduleService.updateStatus(id, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getScheduleDetail(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleDetail(id));
    }
}
