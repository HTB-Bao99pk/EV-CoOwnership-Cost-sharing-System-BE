package swp302.topic6.evcoownership.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import swp302.topic6.evcoownership.dto.GroupSettingsRequest;
import swp302.topic6.evcoownership.entity.CoOwnershipGroup;
import swp302.topic6.evcoownership.entity.GroupMember;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final AdminService adminService;

    // ====== 1️⃣ Duyệt nhóm ======
    @GetMapping("/pending-groups")
    public ResponseEntity<List<CoOwnershipGroup>> getPendingGroups() {
        return ResponseEntity.ok(adminService.getPendingGroups());
    }

    @PostMapping("/approve-group")
    public ResponseEntity<String> approveGroup(@RequestParam Long groupId,
                                               @RequestParam boolean approved,
                                               @RequestParam(required = false) String reason,
                                               @RequestParam Long adminId) {
        // Xử lý logic Duyệt/Từ chối (Approve/Reject)
        if (approved) {
            // Hàm approveGroup trong service chỉ nhận 2 tham số: groupId, adminId
            return ResponseEntity.ok(adminService.approveGroup(groupId, adminId));
        } else {
            // Kiểm tra lý do từ chối
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Lý do từ chối không được để trống!");
            }
            // Hàm rejectGroup nhận 3 tham số: groupId, reason, adminId
            return ResponseEntity.ok(adminService.rejectGroup(groupId, reason, adminId));
        }
    }

    // ====== 2️⃣ Duyệt thành viên ======
    @GetMapping("/pending-members")
    public ResponseEntity<List<GroupMember>> getPendingMembers() {
        // SỬA LỖI TÊN PHƯƠNG THỨC: getPendingJoinRequests -> getPendingMembers
        return ResponseEntity.ok(adminService.getPendingMembers());
    }

    @PostMapping("/approve-member")
    public ResponseEntity<String> approveMember(@RequestParam Long memberId,
                                                @RequestParam boolean approved,
                                                @RequestParam(required = false) String reason) {
        if (approved) {
            // Gọi hàm duyệt thành viên
            return ResponseEntity.ok(adminService.approveMember(memberId));
        } else {
            // Gọi hàm từ chối thành viên
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Lý do từ chối không được để trống!");
            }
            return ResponseEntity.ok(adminService.rejectMember(memberId, reason));
        }
    }

    // ====== 4️⃣ Cập nhật cấu hình nhóm (maxMembers, minOwnershipPercentage)
    @PostMapping("/group/{groupId}/settings")
    public ResponseEntity<String> updateGroupSettings(@PathVariable Long groupId,
                                                      @RequestBody GroupSettingsRequest req) {
        String res = adminService.updateGroupSettings(groupId, req.getMaxMembers(), req.getMinOwnershipPercentage());
        return ResponseEntity.ok(res);
    }

    // ====== 3️⃣ Duyệt xác minh tài khoản ======
    @GetMapping("/pending-users")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(adminService.getPendingUsers());
    }

    @PostMapping("/verify-user")
    public ResponseEntity<String> verifyUser(@RequestParam Long userId,
                                             @RequestParam boolean approved,
                                             @RequestParam(required = false) String reason) {
        if (approved) {
            // Hàm verifyUser chỉ nhận 1 tham số
            return ResponseEntity.ok(adminService.verifyUser(userId));
        } else {
            // Hàm rejectUserVerification nhận 2 tham số
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Lý do từ chối xác minh không được để trống!");
            }
            return ResponseEntity.ok(adminService.rejectUserVerification(userId, reason));
        }
    }

    // ===== Admin user management (list/get/approve/reject/delete)
    @GetMapping("/users")
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return adminService.getUserById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{id}/approve")
    public ResponseEntity<String> approveUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveUser(id));
    }

    @PostMapping("/users/{id}/reject")
    public ResponseEntity<String> rejectUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.rejectUser(id));
    }

    @PostMapping("/users/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deleteUser(id));
    }

    // ====== 🆕 Request Management APIs ======
    
    /**
     * Lấy tất cả yêu cầu tham gia nhóm
     */
    @GetMapping("/requests")
    public ResponseEntity<List<swp302.topic6.evcoownership.dto.AdminRequestResponse>> getAllRequests() {
        return ResponseEntity.ok(adminService.getAllJoinRequests());
    }

    /**
     * Lấy chi tiết yêu cầu tham gia theo ID
     */
    @GetMapping("/requests/{requestId}")
    public ResponseEntity<swp302.topic6.evcoownership.dto.AdminRequestResponse> getRequestById(@PathVariable Long requestId) {
        return adminService.getJoinRequestById(requestId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Chấp nhận yêu cầu tham gia nhóm
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<swp302.topic6.evcoownership.dto.ApiResponse> acceptRequest(@PathVariable Long requestId) {
        try {
            String message = adminService.acceptJoinRequest(requestId);
            return ResponseEntity.ok(new swp302.topic6.evcoownership.dto.ApiResponse(true, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new swp302.topic6.evcoownership.dto.ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Từ chối yêu cầu tham gia nhóm
     */
    @PostMapping("/requests/{requestId}/cancel")
    public ResponseEntity<swp302.topic6.evcoownership.dto.ApiResponse> cancelRequest(
            @PathVariable Long requestId,
            @RequestBody(required = false) swp302.topic6.evcoownership.dto.CancelRequestBody body) {
        try {
            String reason = (body != null) ? body.getReason() : null;
            String message = adminService.cancelJoinRequest(requestId, reason);
            return ResponseEntity.ok(new swp302.topic6.evcoownership.dto.ApiResponse(true, message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new swp302.topic6.evcoownership.dto.ApiResponse(false, e.getMessage()));
        }
    }
}