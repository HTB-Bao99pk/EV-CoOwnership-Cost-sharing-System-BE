package fu.swp.evcs.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.LoginRequest;
import fu.swp.evcs.dto.RegisterRequest;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.LoginService;
import fu.swp.evcs.service.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * ✅ AuthController - Clean controller, chỉ gọi service
 * 
 * Controller KHÔNG chứa logic, chỉ:
 * 1. Nhận request
 * 2. Gọi service
 * 3. Return response (1 dòng duy nhất)
 * 
 * Logic xử lý → Service
 * Exception handling → GlobalExceptionHandler
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final RegisterService registerService;

    /**
     * 🔐 Login - Logic được xử lý trong LoginService
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @RequestBody LoginRequest request, 
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(loginService.login(request, httpRequest));
    }

    /**
     * 📝 Register - Logic được xử lý trong RegisterService
     */
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<String>> register(@ModelAttribute RegisterRequest request) {
        return ResponseEntity.ok(registerService.register(request));
    }

    /**
     * 👤 Get current user - Logic được xử lý trong LoginService
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(loginService.getCurrentUser(user));
    }

    /**
     * 🚪 Logout - Logic được xử lý trong LoginService
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        return ResponseEntity.ok(loginService.logout(request));
    }

    /**
     * 🔍 Check login status - Logic được xử lý trong LoginService
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(
            @AuthenticationPrincipal User user,
            HttpSession session) {
        return ResponseEntity.ok(loginService.checkLoginStatus(user, session));
    }

    /**
     * ℹ️ Info endpoint - Logic được xử lý trong LoginService
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(loginService.getInfo());
    }
}
