package swp302.topic6.evcoownership.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import swp302.topic6.evcoownership.dto.LoginRequest;
import swp302.topic6.evcoownership.dto.LoginResponse;
import swp302.topic6.evcoownership.dto.UserDto;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.service.LoginService;
import swp302.topic6.evcoownership.utils.SessionUtils;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionUtils sessionUtils;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        LoginResponse resp = new LoginResponse();
        try {
            User user = loginService.login(request);

            // Lưu session
            sessionUtils.saveUserSession(session,
                    user.getUserId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole()
            );

            resp.setSuccess(true);
            resp.setMessage("Đăng nhập thành công");
            resp.setUserId(user.getUserId());
            resp.setFullName(user.getFullName());
            resp.setEmail(user.getEmail());
            resp.setRole(user.getRole());
        // Map User -> UserDto to avoid exposing sensitive fields
        UserDto userDto = UserDto.builder()
            .userId(user.getUserId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .cccd(user.getCccd())
            .driverLicense(user.getDriverLicense())
            .birthday(user.getBirthday())
            .role(user.getRole())
            .verificationStatus(user.getVerificationStatus())
            .createdAt(user.getCreatedAt())
            .location(user.getLocation())
            .cccdFrontUrl(user.getCccdFrontUrl())
            .cccdBackUrl(user.getCccdBackUrl())
            .driverLicenseUrl(user.getDriverLicenseUrl())
            .build();

        resp.setUser(userDto);

            return ResponseEntity.ok(resp);
        } catch (RuntimeException ex) {
            resp.setSuccess(false);
            resp.setMessage(ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<LoginResponse> logout(HttpSession session) {
        LoginResponse resp = new LoginResponse();
        if (!sessionUtils.isLoggedIn(session)) {
            resp.setSuccess(false);
            resp.setMessage("Bạn chưa đăng nhập!");
            return ResponseEntity.badRequest().body(resp);
        }
        sessionUtils.clearSession(session);
        resp.setSuccess(true);
        resp.setMessage("Đăng xuất thành công!");
        return ResponseEntity.ok(resp);
    }
}
