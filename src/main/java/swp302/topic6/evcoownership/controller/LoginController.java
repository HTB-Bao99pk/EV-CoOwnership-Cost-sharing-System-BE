package swp302.topic6.evcoownership.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.dto.LoginRequest;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.repository.UserRepository;
import swp302.topic6.evcoownership.utils.SessionUtils;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    private final UserRepository userRepository;
    private final SessionUtils sessionUtils;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request, HttpSession session) {
        // 1️⃣ Tìm người dùng theo email
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return "Tài khoản không tồn tại";
        }

        // 2️⃣ Kiểm tra mật khẩu
        if (!user.getPasswordHash().equals(request.getPassword())) {
            return "Sai mật khẩu";
        }

        // 3️⃣ Lưu session (người dùng đã đăng nhập)
        sessionUtils.saveUserSession(session,
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );

        return "Đăng nhập thành công!";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (!sessionUtils.isLoggedIn(session)) {
            return "Bạn chưa đăng nhập!";
        }
        sessionUtils.clearSession(session);
        return "Đăng xuất thành công!";
    }
}
