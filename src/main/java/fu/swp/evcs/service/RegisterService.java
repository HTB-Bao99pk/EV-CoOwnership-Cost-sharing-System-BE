package fu.swp.evcs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.RegisterRequest;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.exception.BadRequestException;
import fu.swp.evcs.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * ✅ RegisterService - Xử lý đăng ký user mới
 * Sử dụng PasswordEncoder để mã hóa mật khẩu
 * Throw exception thay vì return error string
 */
@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
                .passwordHash(passwordEncoder.encode(request.getPassword())) // ✅ Mã hóa mật khẩu
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
        return ApiResponse.success("Đăng ký thành công! Vui lòng chờ admin xác minh tài khoản.");
    }

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
