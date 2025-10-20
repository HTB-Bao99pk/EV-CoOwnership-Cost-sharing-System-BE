package swp302.topic6.evcoownership.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // ⭐️ TỐI ƯU: Xoá CrossOrigin

import jakarta.servlet.http.HttpSession; // ⭐️ TỐI ƯU: Dùng Session
import lombok.RequiredArgsConstructor;
import swp302.topic6.evcoownership.dto.AdminRequestResponse;
import swp302.topic6.evcoownership.dto.ApiResponse;
import swp302.topic6.evcoownership.dto.CancelRequestBody;
import swp302.topic6.evcoownership.dto.GroupSettingsRequest;
import swp302.topic6.evcoownership.entity.CoOwnershipGroup;
import swp302.topic6.evcoownership.entity.GroupMember;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.service.AdminService;
import swp302.topic6.evcoownership.utils.SessionUtils; // ⭐️ TỐI ƯU: Dùng SessionUtils

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
// ⭐️ TỐI ƯU: Xoá @CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final AdminService adminService;
    private final SessionUtils sessionUtils; // ⭐️ TỐI ƯU: Tiêm SessionUtils

    /**
     * ⭐️ TỐI ƯU: Hàm helper kiểm tra quyền Admin
     * Ném lỗi nếu chưa đăng nhập hoặc không phải admin
     */
    private Long getAdminUserId(HttpSession session) {
        Long adminId = sessionUtils.getUserId(session);
        String role = sessionUtils.getRole(session);

        if (adminId == null) {
            throw new RuntimeException("Vui lòng đăng nhập!");
        }
        if (!"admin".equalsIgnoreCase(role)) {
            throw new RuntimeException("Bạn không có quyền truy cập chức năng này!");
        }
        return adminId;
    }


    // ====== 1️⃣ Duyệt nhóm ======
    @GetMapping("/pending-groups")
    public ResponseEntity<List<CoOwnershipGroup>> getPendingGroups(HttpSession session) {
        getAdminUserId(session); // Chỉ cần check quyền
        return ResponseEntity.ok(adminService.getPendingGroups());
    }

    @PostMapping("/approve-group")
    public ResponseEntity<ApiResponse> approveGroup(@RequestParam Long groupId,
                                                    @RequestParam boolean approved,
                                                    @RequestParam(required = false) String reason,
                                                    HttpSession session) {
        // ⭐️ TỐI ƯU: Lấy adminId từ session an toàn
        Long adminId = getAdminUserId(session);

        String message;
        if (approved) {
            // ⭐️ TỐI ƯU: Xoá try-catch
            message = adminService.approveGroup(groupId, adminId);
        } else {
            // ⭐️ TỐI ƯU: Ném lỗi nếu thiếu lý do
            if (reason == null || reason.trim().isEmpty()) {
                throw new RuntimeException("Lý do từ chối không được để trống!");
            }
            message = adminService.rejectGroup(groupId, reason, adminId);
        }
        return ResponseEntity.ok(new ApiResponse(true, message));
    }

    // ====== 2️⃣ Duyệt thành viên ======
    @GetMapping("/pending-members")
    public ResponseEntity<List<GroupMember>> getPendingMembers(HttpSession session) {
        getAdminUserId(session); // Check quyền
        return ResponseEntity.ok(adminService.getPendingMembers());
    }

    @PostMapping("/approve-member")
    public ResponseEntity<ApiResponse> approveMember(@RequestParam Long memberId,
                                                     @RequestParam boolean approved,
                                                     @RequestParam(required = false) String reason,
                                                     HttpSession session) {
        getAdminUserId(session); // Check quyền

        String message;
        if (approved) {
            message = adminService.approveMember(memberId);
        } else {
            if (reason == null || reason.trim().isEmpty()) {
                throw new RuntimeException("Lý do từ chối không được để trống!");
            }
            message = adminService.rejectMember(memberId, reason);
        }
        return ResponseEntity.ok(new ApiResponse(true, message));
    }

    // ====== 4️⃣ Cập nhật cấu hình nhóm (maxMembers, minOwnershipPercentage)
    @PostMapping("/group/{groupId}/settings")
    public ResponseEntity<ApiResponse> updateGroupSettings(@PathVariable Long groupId,
                                                           @RequestBody GroupSettingsRequest req,
                                                           HttpSession session) {
        getAdminUserId(session); // Check quyền
        String res = adminService.updateGroupSettings(groupId, req.getMaxMembers(), req.getMinOwnershipPercentage());
        return ResponseEntity.ok(new ApiResponse(true, res));
    }

    // ====== 3️⃣ Duyệt xác minh tài khoản ======
    @GetMapping("/pending-users")
    public ResponseEntity<List<User>> getPendingUsers(HttpSession session) {
        getAdminUserId(session); // Check quyền
        return ResponseEntity.ok(adminService.getPendingUsers());
    }

    @PostMapping("/verify-user")
    public ResponseEntity<ApiResponse> verifyUser(@RequestParam Long userId,
                                                  @RequestParam boolean approved,
                                                  @RequestParam(required = false) String reason,
                                                  HttpSession session) {
        getAdminUserId(session); // Check quyền

        String message;
        if (approved) {
            message = adminService.verifyUser(userId);
        } else {
            if (reason == null || reason.trim().isEmpty()) {
                throw new RuntimeException("Lý do từ chối xác minh không được để trống!");
            }
            message = adminService.rejectUserVerification(userId, reason);
        }
        return ResponseEntity.ok(new ApiResponse(true, message));
    }

    // ===== Admin user management (list/get/approve/reject/delete)
    @GetMapping("/users")
    public ResponseEntity<List<User>> listUsers(HttpSession session) {
        getAdminUserId(session); // Check quyền
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id, HttpSession session) {
        getAdminUserId(session); // Check quyền
        // ⭐️ TỐI ƯU: Service sẽ ném lỗi nếu không tìm thấy
        User user = adminService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/{id}/approve")
    public ResponseEntity<ApiResponse> approveUser(@PathVariable Long id, HttpSession session) {
        getAdminUserId(session); // Check quyền
        return ResponseEntity.ok(new ApiResponse(true, adminService.approveUser(id)));
    }

    @PostMapping("/users/{id}/reject")
    public ResponseEntity<ApiResponse> rejectUser(@PathVariable Long id, HttpSession session) {
        getAdminUserId(session); // Check quyền
        return ResponseEntity.ok(new ApiResponse(true, adminService.rejectUser(id)));
    }

    @PostMapping("/users/{id}/delete")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id, HttpSession session) {
        getAdminUserId(session); // Check quyền
        return ResponseEntity.ok(new ApiResponse(true, adminService.deleteUser(id)));
    }


    // ====== 🆕 Request Management APIs ======

    /**
     * ⭐️ TỐI ƯU: Đã xoá các API trùng lặp (getAllRequests, getRequestById, acceptRequest, cancelRequest)
     * vì chúng trùng lặp chức năng với các API /approve-member và /pending-members ở trên.
     * Giữ code đơn giản, mỗi chức năng chỉ nên có 1 API.
     */
}