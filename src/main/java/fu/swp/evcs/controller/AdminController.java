package fu.swp.evcs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.entity.Group;
import fu.swp.evcs.entity.Member;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.AdminService;
import lombok.RequiredArgsConstructor;

/**
 * AdminController - Clean controller, only calls service
 *
 * Controller does NOT contain logic, only:
 * 1. Receives request
 * 2. Calls service
 * 3. Returns response (single line only)
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/pending-groups")
    public ResponseEntity<ApiResponse<List<Group>>> getPendingGroups() {
        List<Group> groups = adminService.getPendingGroups();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách nhóm chờ duyệt thành công", groups));
    }

    @PostMapping("/approve-group")
    public ResponseEntity<ApiResponse<String>> approveGroup(
            @RequestParam Long groupId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal User currentAdmin) {
        String message = adminService.handleGroupApproval(groupId, approved, reason, currentAdmin);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    @GetMapping("/pending-members")
    public ResponseEntity<ApiResponse<List<Member>>> getPendingMembers() {
        List<Member> members = adminService.getPendingMembers();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thành viên chờ duyệt thành công", members));
    }

    @PostMapping("/approve-member")
    public ResponseEntity<ApiResponse<String>> approveMember(
            @RequestParam Long memberId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason) {
        String message = adminService.handleMemberApproval(memberId, approved, reason);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    @GetMapping("/pending-users")
    public ResponseEntity<ApiResponse<List<User>>> getPendingUsers() {
        List<User> users = adminService.getPendingUsers();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng chờ xác minh thành công", users));
    }

    @PostMapping("/verify-user")
    public ResponseEntity<ApiResponse<String>> verifyUser(
            @RequestParam Long userId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason) {
        String message = adminService.handleUserVerification(userId, approved, reason);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    @GetMapping("/pending-vehicles")
    public ResponseEntity<ApiResponse<List<fu.swp.evcs.entity.Vehicle>>> getPendingVehicles() {
        List<fu.swp.evcs.entity.Vehicle> vehicles = adminService.getPendingVehicles();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách xe chờ duyệt thành công", vehicles));
    }

    @PostMapping("/approve-vehicle")
    public ResponseEntity<ApiResponse<String>> approveVehicle(
            @RequestParam Long vehicleId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal User currentAdmin) {
        String message = adminService.handleVehicleApproval(vehicleId, approved, reason, currentAdmin);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }
}