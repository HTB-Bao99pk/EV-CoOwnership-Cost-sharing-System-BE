package swp302.topic6.evcoownership.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // ⭐️ TỐI ƯU: Xoá CrossOrigin

import java.util.List;

import jakarta.servlet.http.HttpSession; // ⭐️ TỐI ƯU: Dùng Session
import lombok.RequiredArgsConstructor;
import swp302.topic6.evcoownership.dto.ApiResponse;
import swp302.topic6.evcoownership.dto.CreateGroupRequest;
import swp302.topic6.evcoownership.dto.EditGroupRequest;
import swp302.topic6.evcoownership.dto.GroupDetailResponse;
import swp302.topic6.evcoownership.dto.GroupMemberResponse;
import swp302.topic6.evcoownership.dto.JoinGroupRequest;
import swp302.topic6.evcoownership.dto.UserGroupResponse;
import swp302.topic6.evcoownership.service.GroupService;
import swp302.topic6.evcoownership.utils.SessionUtils; // ⭐️ TỐI ƯU: Dùng SessionUtils

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
// ⭐️ TỐI ƯU: Xoá @CrossOrigin(origins = "http://localhost:3000")
// Đã cấu hình tập trung tại CorsConfig.java
public class GroupController {

    private final GroupService groupService;
    private final SessionUtils sessionUtils; // ⭐️ TỐI ƯU: Tiêm SessionUtils

    /**
     * ⭐️ TỐI ƯU: Hàm helper kiểm tra đăng nhập và lấy userId
     * Ném lỗi nếu chưa đăng nhập, GlobalExceptionHandler sẽ bắt
     */
    private Long getAuthenticatedUserId(HttpSession session) {
        Long userId = sessionUtils.getUserId(session);
        if (userId == null) {
            throw new RuntimeException("Vui lòng đăng nhập để thực hiện chức năng này!");
        }
        return userId;
    }

    /**
     * API tạo nhóm chia sẻ xe
     * Chỉ chủ sở hữu xe mới được phép tạo nhóm
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createGroup(
            @RequestBody CreateGroupRequest request,
            HttpSession session // ⭐️ TỐI ƯU: Dùng session
            // ⭐️ TỐI ƯU: Xóa @RequestParam Long userId
    ) {
        // ⭐️ TỐI ƯU: Lấy userId từ session an toàn
        Long userId = getAuthenticatedUserId(session);

        // ⭐️ TỐI ƯU: Xoá try-catch, GlobalExceptionHandler sẽ bắt lỗi
        String result = groupService.createGroup(request, userId);
        return ResponseEntity.ok(new ApiResponse(true, result));
    }

    /**
     * API trả về số lượng thành viên active trong nhóm
     */
    @GetMapping("/{groupId}/members/count")
    public ResponseEntity<Integer> getActiveMemberCount(@PathVariable Long groupId) {
        // ⭐️ TỐI ƯU: Xoá try-catch
        int count = groupService.countActiveMembers(groupId);
        return ResponseEntity.ok(count);
    }

    /**
     * Yêu cầu tham gia nhóm (gửi requestedPercentage)
     */
    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse> requestJoin(@PathVariable Long groupId,
                                                   @RequestBody JoinGroupRequest body,
                                                   HttpSession session) {
        // ⭐️ TỐI ƯU: Lấy userId từ session, KHÔNG tin tưởng userId từ body
        Long userId = getAuthenticatedUserId(session);

        // ⭐️ TỐI ƯU: Xoá try-catch
        String result = groupService.requestToJoinGroup(groupId, userId, body.getRequestedPercentage());
        return ResponseEntity.ok(new ApiResponse(true, result));
    }

    /**
     * Trả về chi tiết nhóm kèm số lượng thành viên active
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponse> getGroupDetail(@PathVariable Long groupId) {
        // ⭐️ TỐI ƯU: Xoá try-catch. Service sẽ ném lỗi nếu không tìm thấy
        GroupDetailResponse resp = groupService.getGroupDetail(groupId);
        return ResponseEntity.ok(resp);
    }

    // ====== 🆕 User Group Management APIs ======

    /**
     * Lấy danh sách nhóm CỦA TÔI (của user đang đăng nhập)
     */
    // ⭐️ TỐI ƯU: Đổi path /user/{userId} -> /my-groups cho chuẩn REST
    @GetMapping("/my-groups")
    public ResponseEntity<List<UserGroupResponse>> getMyGroups(HttpSession session) {
        Long userId = getAuthenticatedUserId(session);
        return ResponseEntity.ok(groupService.getUserGroups(userId));
    }

    /**
     * Chỉnh sửa thông tin nhóm (chỉ admin nhóm)
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse> editGroup(
            @PathVariable Long groupId,
            @RequestBody EditGroupRequest request,
            HttpSession session) { // ⭐️ TỐI ƯU: Dùng session
        Long userId = getAuthenticatedUserId(session);
        // ⭐️ TỐI ƯU: Xoá try-catch
        String message = groupService.editGroup(groupId, request, userId);
        return ResponseEntity.ok(new ApiResponse(true, message));
    }

    /**
     * Xóa nhóm (chỉ admin nhóm)
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse> deleteGroup(
            @PathVariable Long groupId,
            HttpSession session) { // ⭐️ TỐI ƯU: Dùng session
        Long userId = getAuthenticatedUserId(session);
        // ⭐️ TỐI ƯU: Xoá try-catch
        String message = groupService.deleteGroup(groupId, userId);
        return ResponseEntity.ok(new ApiResponse(true, message));
    }

    /**
     * Chấp nhận yêu cầu tham gia (admin nhóm)
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<ApiResponse> acceptJoinRequest(
            @PathVariable Long requestId,
            HttpSession session) { // ⭐️ TỐI ƯU: Dùng session
        Long userId = getAuthenticatedUserId(session); // userId của admin nhóm
        // ⭐️ TỐI ƯU: Xoá try-catch
        String message = groupService.acceptJoinRequest(requestId, userId);
        return ResponseEntity.ok(new ApiResponse(true, message));
    }

    /**
     * Xóa thành viên khỏi nhóm (admin nhóm)
     */
    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<ApiResponse> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            HttpSession session) { // ⭐️ TỐI ƯU: Dùng session
        Long userId = getAuthenticatedUserId(session); // userId của admin nhóm
        // ⭐️ TỐI ƯU: Xoá try-catch
        String message = groupService.removeMember(groupId, memberId, userId);
        return ResponseEntity.ok(new ApiResponse(true, message));
    }

    /**
     * Lấy danh sách thành viên trong nhóm
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(
            @PathVariable Long groupId,
            HttpSession session) { // ⭐️ TỐI ƯU: Dùng session
        Long userId = getAuthenticatedUserId(session); // userId của người xem
        // ⭐️ TỐI ƯU: Xoá try-catch
        return ResponseEntity.ok(groupService.getGroupMembers(groupId, userId));
    }

    /**
     * Lấy thông tin chi tiết một thành viên
     */
    @GetMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<GroupMemberResponse> getGroupMemberById(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            HttpSession session) { // ⭐️ TỐI ƯU: Dùng session
        Long userId = getAuthenticatedUserId(session); // userId của người xem

        // ⭐️ TỐI ƯU: Service sẽ ném lỗi nếu ko tìm thấy
        GroupMemberResponse member = groupService.getGroupMemberById(groupId, memberId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành viên này"));
        return ResponseEntity.ok(member);
    }
}