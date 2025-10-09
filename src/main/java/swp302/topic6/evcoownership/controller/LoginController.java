package swp302.topic6.evcoownership.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.dto.LoginRequest;
import swp302.topic6.evcoownership.dto.LoginResponse;
import swp302.topic6.evcoownership.service.LoginService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {
    private final LoginService loginService;

    // Đăng nhập và tạo session
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        LoginResponse response = loginService.login(request);
        if (response.isSuccess()) {
            // Lưu thông tin user vào session
            session.setAttribute("userId", response.getUserId());
            session.setAttribute("email", response.getEmail());
            session.setAttribute("role", response.getRole());
        }
        return ResponseEntity.ok(response);
    }

    // Kiểm tra session hiện tại (test)
    @GetMapping("/session")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Object userId = session.getAttribute("userId");
        Object email = session.getAttribute("email");
        Object role = session.getAttribute("role");

        if (userId == null) {
            return ResponseEntity.status(401).body("Chưa đăng nhập !");
        }

        return ResponseEntity.ok(new Object() {
            public final Object id = userId;
            public final Object userEmail = email;
            public final Object userRole = role;
        });
    }

    // Đăng xuất (xóa session)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Đã đăng xuất thành công!");
    }
}
