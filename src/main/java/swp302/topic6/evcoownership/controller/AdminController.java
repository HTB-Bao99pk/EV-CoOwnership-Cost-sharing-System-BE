package swp302.topic6.evcoownership.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.entity.CoOwnershipGroup;
import swp302.topic6.evcoownership.entity.GroupMember;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.service.AdminService;

import java.util.List;

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
}