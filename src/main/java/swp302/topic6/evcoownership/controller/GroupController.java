package swp302.topic6.evcoownership.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;
import swp302.topic6.evcoownership.dto.CreateGroupRequest;
import swp302.topic6.evcoownership.dto.GroupDetailResponse;
import swp302.topic6.evcoownership.service.GroupService;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
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

    // ====== 🆕 User Group Management APIs ======

    /**
     * Lấy danh sách nhóm của user hiện tại
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<swp302.topic6.evcoownership.dto.UserGroupResponse>> getMyGroups(@PathVariable Long userId) {
        return ResponseEntity.ok(groupService.getUserGroups(userId));
    }

    /**
     * Chỉnh sửa thông tin nhóm (chỉ admin nhóm)
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<swp302.topic6.evcoownership.dto.ApiResponse> editGroup(
            @PathVariable Long groupId,
            @RequestBody swp302.topic6.evcoownership.dto.EditGroupRequest request,
            @RequestParam Long userId) {
        try {
            String message = groupService.editGroup(groupId, request, userId);
            return ResponseEntity.ok(new swp302.topic6.evcoownership.dto.ApiResponse(true, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new swp302.topic6.evcoownership.dto.ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Xóa nhóm (chỉ admin nhóm)
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<swp302.topic6.evcoownership.dto.ApiResponse> deleteGroup(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        try {
            String message = groupService.deleteGroup(groupId, userId);
            return ResponseEntity.ok(new swp302.topic6.evcoownership.dto.ApiResponse(true, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new swp302.topic6.evcoownership.dto.ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Chấp nhận yêu cầu tham gia (admin nhóm)
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<swp302.topic6.evcoownership.dto.ApiResponse> acceptJoinRequest(
            @PathVariable Long requestId,
            @RequestParam Long userId) {
        try {
            String message = groupService.acceptJoinRequest(requestId, userId);
            return ResponseEntity.ok(new swp302.topic6.evcoownership.dto.ApiResponse(true, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new swp302.topic6.evcoownership.dto.ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Xóa thành viên khỏi nhóm (admin nhóm)
     */
    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<swp302.topic6.evcoownership.dto.ApiResponse> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @RequestParam Long userId) {
        try {
            String message = groupService.removeMember(groupId, memberId, userId);
            return ResponseEntity.ok(new swp302.topic6.evcoownership.dto.ApiResponse(true, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new swp302.topic6.evcoownership.dto.ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Lấy danh sách thành viên trong nhóm
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<swp302.topic6.evcoownership.dto.GroupMemberResponse>> getGroupMembers(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        try {
            return ResponseEntity.ok(groupService.getGroupMembers(groupId, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy thông tin chi tiết một thành viên
     */
    @GetMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<swp302.topic6.evcoownership.dto.GroupMemberResponse> getGroupMemberById(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @RequestParam Long userId) {
        return groupService.getGroupMemberById(groupId, memberId, userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
