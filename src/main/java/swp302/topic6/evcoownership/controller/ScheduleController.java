package swp302.topic6.evcoownership.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.entity.Schedule;
import swp302.topic6.evcoownership.service.ScheduleService;

import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody Schedule schedule) {
        Map<String, Object> result = scheduleService.createBooking(schedule);
        return ResponseEntity.ok(result); // luôn trả 200
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getSchedulesByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByGroup(groupId));
    }
}
