package swp302.topic6.evcoownership.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.dto.LoginRequest;
import swp302.topic6.evcoownership.dto.LoginResponse;
import swp302.topic6.evcoownership.service.LoginService;
import swp302.topic6.evcoownership.utils.SessionUtils;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    private final LoginService loginService;
    private final SessionUtils sessionUtils;

    // 👉 Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        LoginResponse response = loginService.login(request);

        if (response.isSuccess()) {
            // Lưu thông tin đăng nhập vào session
            sessionUtils.saveUserSession(session,
                    response.getUserId(),
                    response.getEmail(),
                    response.getFullName(),
                    response.getRole()
            );
            System.out.println("✅ Đăng nhập thành công - Lưu session userId = " + response.getUserId());
        }

        return ResponseEntity.ok(response);
    }

    // 👉 Lấy thông tin người dùng hiện tại
    @GetMapping("/current-user")
    public ResponseEntity<String> getCurrentUser(HttpSession session) {
        if (!sessionUtils.isLoggedIn(session)) {
            return ResponseEntity.ok("❌ Bạn chưa đăng nhập!");
        }

        return ResponseEntity.ok("👤 Người dùng hiện tại: "
                + sessionUtils.getFullName(session)
                + " | Email: " + sessionUtils.getEmail(session)
                + " | Role: " + sessionUtils.getRole(session)
        );
    }

    // 👉 Đăng xuất
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        if (!sessionUtils.isLoggedIn(session)) {
            return ResponseEntity.ok("Bạn chưa đăng nhập!");
        }

        sessionUtils.clearSession(session);
        return ResponseEntity.ok("✅ Đăng xuất thành công!");
    }
}
