package swp302.topic6.evcoownership.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import swp302.topic6.evcoownership.dto.CreateGroupRequest;
import swp302.topic6.evcoownership.dto.GroupDetailResponse;
import swp302.topic6.evcoownership.service.GroupService;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * API tạo nhóm chia sẻ xe
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
     * API trả về số lượng thành viên active trong nhóm
     */
    @GetMapping("/{groupId}/members/count")
    public ResponseEntity<Integer> getActiveMemberCount(@PathVariable Long groupId) {
        try {
            int count = groupService.countActiveMembers(groupId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Yêu cầu tham gia nhóm (gửi requestedPercentage)
     */
    @PostMapping("/{groupId}/join")
    public ResponseEntity<String> requestJoin(@PathVariable Long groupId,
                                              @RequestBody swp302.topic6.evcoownership.dto.JoinGroupRequest body) {
        try {
            String result = groupService.requestToJoinGroup(groupId, body.getUserId(), body.getRequestedPercentage());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi gửi yêu cầu: " + e.getMessage());
        }
    }

    /**
     * Trả về chi tiết nhóm kèm số lượng thành viên active
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponse> getGroupDetail(@PathVariable Long groupId) {
        GroupDetailResponse resp = groupService.getGroupDetail(groupId);
        if (resp == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resp);
    }
}
