package fu.swp.evcs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fu.swp.evcs.dto.CreateGroupRequest;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.GroupJoinService;
import fu.swp.evcs.service.GroupService;
import lombok.RequiredArgsConstructor;

/**
 * ✅ GroupController - Clean controller, chỉ gọi service
 * 
 * Controller KHÔNG chứa logic, chỉ:
 * 1. Nhận request + @AuthenticationPrincipal User
 * 2. Gọi service (service xử lý validation + logic)
 * 3. Return response (1 dòng duy nhất)
 */
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final GroupJoinService groupJoinService;

    /**
     * 🟢 Tạo nhóm chia sẻ xe
     */
    @PostMapping("/create")
    public ResponseEntity<String> createGroup(
            @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(groupService.createGroup(request, currentUser));
    }

    /**
     * 🟡 Gửi yêu cầu tham gia nhóm
     */
    @PostMapping("/{groupId}/join")
    public ResponseEntity<String> requestJoinGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(groupJoinService.requestJoinGroup(groupId, currentUser));
    }
}
