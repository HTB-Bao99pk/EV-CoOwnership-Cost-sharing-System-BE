package swp302.topic6.evcoownership.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.service.GroupJoinService;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GroupJoinController {

    private final GroupJoinService groupJoinService;

    // 🟩 Người dùng gửi yêu cầu tham gia nhóm
    @PostMapping("/{groupId}/join")
    public String requestJoinGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        return groupJoinService.requestJoinGroup(groupId, userId);
    }

    // 🟦 Admin hoặc chủ nhóm duyệt hoặc từ chối
    @PutMapping("/members/{memberId}/review")
    public String reviewJoinRequest(@PathVariable Long memberId, @RequestParam boolean approved) {
        return groupJoinService.reviewJoinRequest(memberId, approved);
    }
}
