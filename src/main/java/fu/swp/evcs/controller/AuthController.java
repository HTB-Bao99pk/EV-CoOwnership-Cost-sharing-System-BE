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
 * AuthController - Clean controller, only calls service
 *
 * Controller does NOT contain logic, only:
 * 1. Receives request
 * 2. Calls service
 * 3. Returns response (single line only)
 *
 * Logic processing → Service
 * Exception handling → GlobalExceptionHandler
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final RegisterService registerService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @RequestBody LoginRequest request, 
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(loginService.login(request, httpRequest));
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<String>> register(@ModelAttribute RegisterRequest request) {
        return ResponseEntity.ok(registerService.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(loginService.getCurrentUser(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        return ResponseEntity.ok(loginService.logout(request));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(
            @AuthenticationPrincipal User user,
            HttpSession session) {
        return ResponseEntity.ok(loginService.checkLoginStatus(user, session));
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(loginService.getInfo());
    }
}
