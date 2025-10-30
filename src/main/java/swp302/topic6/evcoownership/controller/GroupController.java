package swp302.topic6.evcoownership.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.dto.CreateGroupRequest;
import swp302.topic6.evcoownership.service.GroupJoinService;
import swp302.topic6.evcoownership.service.GroupService;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GroupController {

    private final GroupService groupService;
    private final GroupJoinService groupJoinService;

    /**
     * 🟢 API tạo nhóm chia sẻ xe
     * Chỉ chủ sở hữu xe mới được phép tạo nhóm
     */
    @PostMapping("/create")
    public ResponseEntity<String> createGroup(
            @RequestBody CreateGroupRequest request,
            @RequestParam Long userId // giả lập ID người tạo nhóm (sẽ dùng Auth thực sau)
    ) {
        try {
            String result = groupService.createGroup(request, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi khi tạo nhóm: " + e.getMessage());
        }
    }

    /**
     * 🟡 Người dùng gửi yêu cầu tham gia nhóm
     */
    @PostMapping("/{groupId}/join")
    public ResponseEntity<String> requestJoinGroup(
            @PathVariable Long groupId,
            @RequestParam Long userId
    ) {
        try {
            String result = groupJoinService.requestJoinGroup(groupId, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Không thể gửi yêu cầu: " + e.getMessage());
        }
    }


}
