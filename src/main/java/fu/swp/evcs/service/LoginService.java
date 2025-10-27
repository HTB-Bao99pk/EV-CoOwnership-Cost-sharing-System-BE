package fu.swp.evcs.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.LoginRequest;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * ✅ LoginService - Xử lý tất cả logic authentication
 * 
 * Controller chỉ gọi service, tất cả logic ở đây
 */
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;

    /**
     * Login - Xử lý authentication và tạo session
     */
    public ApiResponse<Map<String, Object>> login(LoginRequest request, HttpServletRequest httpRequest) {
        // 1. Authenticate
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        // 2. Lưu vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 3. Lưu vào session
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
            SecurityContextHolder.getContext()
        );
        
        // 4. Lấy user info
        User user = (User) authentication.getPrincipal();
        
        // 5. Tạo response
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getId());
        userData.put("email", user.getEmail());
        userData.put("fullName", user.getFullName());
        userData.put("role", user.getRole());
        userData.put("verificationStatus", user.getVerificationStatus());
        
        return ApiResponse.success("Đăng nhập thành công", userData);
    }

    /**
     * Get current user - Kiểm tra authentication và trả về user info
     */
    public ApiResponse<Map<String, Object>> getCurrentUser(User user) {
        if (user == null) {
            throw new UnauthorizedException("Chưa đăng nhập");
        }
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getId());
        userData.put("email", user.getEmail());
        userData.put("fullName", user.getFullName());
        userData.put("role", user.getRole());
        userData.put("verificationStatus", user.getVerificationStatus());
        userData.put("location", user.getLocation());
        
        return ApiResponse.success(userData);
    }

    /**
     * Logout - Xóa session và SecurityContext
     */
    public ApiResponse<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        
        return ApiResponse.success("Đăng xuất thành công");
    }

    /**
     * Check login status
     */
    public Map<String, Object> checkLoginStatus(User user, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        response.put("isAuthenticated", user != null);
        response.put("sessionId", session.getId());
        
        if (user != null) {
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
        }
        
        return response;
    }

    /**
     * API info
     */
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "EV Co-Ownership API");
        info.put("version", "3.0 - Clean Architecture");
        info.put("loginEndpoint", "POST /api/auth/login");
        info.put("registerEndpoint", "POST /api/auth/register");
        info.put("logoutEndpoint", "POST /api/auth/logout");
        info.put("meEndpoint", "GET /api/auth/me");
        return info;
    }
}
