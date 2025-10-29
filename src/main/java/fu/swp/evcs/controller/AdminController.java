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
    public ResponseEntity<List<Group>> getPendingGroups() {
        return ResponseEntity.ok(adminService.getPendingGroups());
    }

    @PostMapping("/approve-group")
    public ResponseEntity<String> approveGroup(
            @RequestParam Long groupId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal User currentAdmin) {
        return ResponseEntity.ok(adminService.handleGroupApproval(groupId, approved, reason, currentAdmin));
    }

    @GetMapping("/pending-members")
    public ResponseEntity<List<Member>> getPendingMembers() {
        return ResponseEntity.ok(adminService.getPendingMembers());
    }

    @PostMapping("/approve-member")
    public ResponseEntity<String> approveMember(
            @RequestParam Long memberId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(adminService.handleMemberApproval(memberId, approved, reason));
    }

    @GetMapping("/pending-users")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(adminService.getPendingUsers());
    }

    @PostMapping("/verify-user")
    public ResponseEntity<String> verifyUser(
            @RequestParam Long userId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(adminService.handleUserVerification(userId, approved, reason));
    }
}