package swp302.topic6.evcoownership.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.dto.ApiResponse;
import swp302.topic6.evcoownership.dto.ScheduleRequest;
import swp302.topic6.evcoownership.dto.ScheduleResponse;
import swp302.topic6.evcoownership.service.ScheduleService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse>> create(@RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.createSchedule(request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ScheduleResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.cancelSchedule(id));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<ApiResponse<ScheduleResponse>> returnVehicle(
            @PathVariable Long id,
            @RequestParam double batteryLevel,
            @RequestParam LocalDateTime actualEndTime
    ) {
        return ResponseEntity.ok(scheduleService.returnVehicle(id, batteryLevel, actualEndTime));
    }

    @GetMapping("/group/{groupId}/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGroupSummary(@PathVariable Long groupId) {
        return ResponseEntity.ok(scheduleService.getGroupSummary(groupId));
    }
}
