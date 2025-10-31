package fu.swp.evcs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping; // Import DELETE
import org.springframework.web.bind.annotation.PutMapping;    // Import PUT
import org.springframework.web.bind.annotation.PatchMapping; // Import PATCH

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.VerificationRequest; // DTO mới
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // SỬA ĐỔI: Thêm Query Param để lọc (thay thế GET /api/admin/pending-users)
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(
            @RequestParam(required = false) String verificationStatus) {

        List<User> users;
        if (verificationStatus != null) {
            users = userService.getUsersByVerificationStatus(verificationStatus);
        } else {
            users = userService.getAllUsers();
        }
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", user));
    }

    // Tạm thời giữ lại PUT nhận Entity (như code cũ)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @RequestBody User userUpdate,
            @AuthenticationPrincipal User currentUser) {
        User updated = userService.updateUser(id, userUpdate, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật người dùng thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        userService.deleteUser(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công", null));
    }

    // NEW: API Xác minh Admin (Thay thế POST /api/admin/verify-user)
    @PatchMapping("/{userId}/verification")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> handleUserVerification(
            @PathVariable Long userId,
            @RequestBody VerificationRequest request) {

        String message = userService.handleUserVerification(
                userId,
                request.isApproved()
        );
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }
}