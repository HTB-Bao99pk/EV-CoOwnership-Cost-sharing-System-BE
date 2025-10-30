package fu.swp.evcs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.LoginRequest;
import fu.swp.evcs.dto.LoginResponseDto;
import fu.swp.evcs.dto.LoginStatusDto;
import fu.swp.evcs.dto.RegisterRequest;
import fu.swp.evcs.dto.UpdateProfileRequest;
import fu.swp.evcs.dto.UserResponseDto;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @RequestBody LoginRequest request, 
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<String>> register(@ModelAttribute RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

        @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.getCurrentUser(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(user, request));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<LoginStatusDto>> checkLoginStatus(
            @AuthenticationPrincipal User user,
            HttpSession session) {
        return ResponseEntity.ok(authService.checkLoginStatus(user, session));
    }
}
