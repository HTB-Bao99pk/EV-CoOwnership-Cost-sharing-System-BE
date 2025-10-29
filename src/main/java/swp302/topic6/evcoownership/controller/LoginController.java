package swp302.topic6.evcoownership.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.dto.LoginRequest;
import swp302.topic6.evcoownership.dto.LoginResponse;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.service.LoginService;
import swp302.topic6.evcoownership.utils.SessionUtils;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    private final LoginService loginService;
    private final SessionUtils sessionUtils;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            User user = loginService.login(request);

            // Lưu session khi đăng nhập thành công
            sessionUtils.saveUserSession(session,
                    user.getUserId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole()
            );

            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("Đăng nhập thành công!");
            response.setUserId(user.getUserId());
            response.setFullName(user.getFullName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setUser(user);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<LoginResponse> logout(HttpSession session) {
        LoginResponse response = new LoginResponse();

        if (!sessionUtils.isLoggedIn(session)) {
            response.setSuccess(false);
            response.setMessage("Bạn chưa đăng nhập!");
            return ResponseEntity.badRequest().body(response);
        }

        sessionUtils.clearSession(session);
        response.setSuccess(true);
        response.setMessage("Đăng xuất thành công!");
        return ResponseEntity.ok(response);
    }
}
