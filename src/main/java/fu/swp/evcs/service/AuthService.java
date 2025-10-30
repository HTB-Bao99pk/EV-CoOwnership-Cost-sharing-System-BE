package fu.swp.evcs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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
import fu.swp.evcs.dto.UpdateProfileRequest;
import fu.swp.evcs.dto.UserResponseDto;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.exception.BadRequestException;
import fu.swp.evcs.exception.UnauthorizedException;
import fu.swp.evcs.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    public ApiResponse<LoginResponseDto> login(LoginRequest request, HttpServletRequest httpRequest) {
        validateLoginRequest(request);
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
            SecurityContextHolder.getContext()
        );
        session.setMaxInactiveInterval(30 * 60);
        
        User user = (User) authentication.getPrincipal();
        
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

    public ApiResponse<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        
        return ApiResponse.success("Đăng xuất thành công", null);
    }

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

    public ApiResponse<UserResponseDto> updateProfile(User currentUser, UpdateProfileRequest request) {
        if (currentUser == null) {
            throw new UnauthorizedException("Chưa đăng nhập");
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UnauthorizedException("User không tồn tại"));

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            if (request.getFullName().trim().length() < 3) {
                throw new BadRequestException("Họ tên phải có ít nhất 3 ký tự");
            }
            user.setFullName(request.getFullName().trim());
        }

        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            user.setLocation(request.getLocation().trim());
        }

        if (request.getBirthday() != null) {
            if (request.getBirthday().isAfter(java.time.LocalDate.now().minusYears(18))) {
                throw new BadRequestException("Người dùng phải từ 18 tuổi trở lên");
            }
            user.setBirthday(request.getBirthday());
        }

        if (request.getDriverLicense() != null && !request.getDriverLicense().trim().isEmpty()) {
            user.setDriverLicense(request.getDriverLicense().trim());
        }

        userRepository.save(user);

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

        return ApiResponse.success("Cập nhật thông tin thành công", responseDto);
    }

    public ApiResponse<String> register(RegisterRequest request) {
        validateRegisterRequest(request);
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại trong hệ thống");
        }

        if (userRepository.existsByCccd(request.getCccd())) {
            throw new BadRequestException("Số CCCD đã được đăng ký");
        }

        String cccdFrontUrl = validateAndSaveFile(request.getCccdFront(), "cccd_front");
        String cccdBackUrl = validateAndSaveFile(request.getCccdBack(), "cccd_back");
        String driverLicenseUrl = validateAndSaveFile(request.getDriverLicenseImg(), "driver_license");

        User newUser = User.builder()
                .fullName(request.getFullName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .cccd(request.getCccd().trim())
                .driverLicense(request.getDriverLicense().trim())
                .birthday(request.getBirthday())
                .location(request.getLocation().trim())
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

    private void validateLoginRequest(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email không được để trống");
        }
        
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new BadRequestException("Mật khẩu không được để trống");
        }
    }
    
    private void validateRegisterRequest(RegisterRequest request) {
        validateFullName(request.getFullName());
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        validateCccd(request.getCccd());
        validateDriverLicense(request.getDriverLicense());
        validateBirthday(request.getBirthday());
        validateLocation(request.getLocation());
    }
    
    private void validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new BadRequestException("Họ tên không được để trống");
        }
        if (fullName.trim().length() < 3) {
            throw new BadRequestException("Họ tên phải có ít nhất 3 ký tự");
        }
    }
    
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email không được để trống");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Email không đúng định dạng");
        }
    }
    
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new BadRequestException("Mật khẩu không được để trống");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new BadRequestException("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BadRequestException("Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường và 1 số");
        }
    }
    
    private void validateCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new BadRequestException("Số CCCD không được để trống");
        }
        if (!cccd.matches("\\d{12}")) {
            throw new BadRequestException("Số CCCD phải có đúng 12 chữ số");
        }
    }
    
    private void validateDriverLicense(String driverLicense) {
        if (driverLicense == null || driverLicense.trim().isEmpty()) {
            throw new BadRequestException("Số GPLX không được để trống");
        }
    }
    
    private void validateBirthday(java.time.LocalDate birthday) {
        if (birthday == null) {
            throw new BadRequestException("Ngày sinh không được để trống");
        }
        if (birthday.isAfter(java.time.LocalDate.now().minusYears(18))) {
            throw new BadRequestException("Người dùng phải từ 18 tuổi trở lên");
        }
    }
    
    private void validateLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new BadRequestException("Địa chỉ không được để trống");
        }
    }

    /**
     * Validates file security (size, type, extension) and saves to disk with safe filename.
     * Generates UUID-based filename to prevent path traversal and filename conflicts.
     */
    private String validateAndSaveFile(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Kích thước file không được vượt quá 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Chỉ chấp nhận file ảnh định dạng JPG, JPEG, PNG");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BadRequestException("Tên file không hợp lệ");
        }
        
        String extension = getFileExtension(originalFilename);
        if (!Arrays.asList("jpg", "jpeg", "png").contains(extension.toLowerCase())) {
            throw new BadRequestException("Phần mở rộng file không được phép");
        }

        try {
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);

            String safeFileName = prefix + "_" 
                    + UUID.randomUUID().toString() + "_" 
                    + System.currentTimeMillis() 
                    + "." + extension;
            
            Path filePath = uploadDir.resolve(safeFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
            
        } catch (IOException e) {
            throw new BadRequestException("Lỗi khi lưu file: " + e.getMessage());
        }
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
