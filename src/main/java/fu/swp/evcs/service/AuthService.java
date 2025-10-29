package fu.swp.evcs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.LoginRequest;
import fu.swp.evcs.dto.LoginResponseDto;
import fu.swp.evcs.dto.LoginStatusDto;
import fu.swp.evcs.dto.RegisterRequest;
import fu.swp.evcs.dto.UserResponseDto;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.exception.BadRequestException;
import fu.swp.evcs.exception.UnauthorizedException;
import fu.swp.evcs.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * ✅ AuthService - Hợp nhất LoginService và RegisterService
 * 
 * Xử lý tất cả logic authentication:
 * - Login, Logout, Check status
 * - Register user mới
 * - Get current user info
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ==================== LOGIN ====================
    
    /**
     * Login - Xử lý authentication và tạo session
     */
    public ApiResponse<LoginResponseDto> login(LoginRequest request, HttpServletRequest httpRequest) {
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
        
        // 5. Tạo response DTO
        LoginResponseDto responseDto = LoginResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .verificationStatus(user.getVerificationStatus())
                .sessionId(session.getId())
                .build();
        
        return ApiResponse.success("Đăng nhập thành công", responseDto);
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
        
        return ApiResponse.success("Đăng xuất thành công", null);
    }

    /**
     * Get current user - Kiểm tra authentication và trả về user info
     */
    public ApiResponse<UserResponseDto> getCurrentUser(User user) {
        if (user == null) {
            throw new UnauthorizedException("Chưa đăng nhập");
        }
        
        UserResponseDto responseDto = UserResponseDto.builder()
                .id(user.getId())
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
        
        return ApiResponse.success("Thông tin người dùng", responseDto);
    }

    /**
     * Check login status
     */
    public ApiResponse<LoginStatusDto> checkLoginStatus(User user, HttpSession session) {
        LoginStatusDto.LoginStatusDtoBuilder builder = LoginStatusDto.builder()
                .isAuthenticated(user != null)
                .sessionId(session.getId());
        
        if (user != null) {
            builder.userId(user.getId())
                   .email(user.getEmail())
                   .role(user.getRole());
        }
        
        return ApiResponse.success("Trạng thái đăng nhập", builder.build());
    }

    // ==================== REGISTER ====================
    
    /**
     * Register - Đăng ký user mới
     */
    public ApiResponse<String> register(RegisterRequest request) {
        // 1️⃣ Kiểm tra email trùng
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại!");
        }

        // 2️⃣ Kiểm tra CCCD trùng
        if (userRepository.existsByCccd(request.getCccd())) {
            throw new BadRequestException("CCCD đã tồn tại!");
        }

        // 3️⃣ Lưu file ảnh (nếu có)
        String cccdFrontUrl = saveFile(request.getCccdFront(), "cccd_front");
        String cccdBackUrl = saveFile(request.getCccdBack(), "cccd_back");
        String driverLicenseUrl = saveFile(request.getDriverLicenseImg(), "driver_license");

        // 4️⃣ Tạo đối tượng User
        User newUser = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .cccd(request.getCccd())
                .driverLicense(request.getDriverLicense())
                .birthday(request.getBirthday())
                .location(request.getLocation())
                .role("user")
                .verificationStatus("pending")
                .createdAt(LocalDateTime.now())
                .cccdFrontUrl(cccdFrontUrl)
                .cccdBackUrl(cccdBackUrl)
                .driverLicenseUrl(driverLicenseUrl)
                .build();

        userRepository.save(newUser);
        return ApiResponse.success("Đăng ký thành công! Vui lòng chờ admin xác minh tài khoản.", null);
    }

    // ==================== HELPERS ====================
    
    private String saveFile(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) return null;

        try {
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);

            String fileName = prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new BadRequestException("Lỗi khi lưu file: " + e.getMessage());
        }
    }
}
