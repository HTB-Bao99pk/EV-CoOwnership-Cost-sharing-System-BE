package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swp302.topic6.evcoownership.dto.RegisterRequest;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.repository.UserRepository;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;

    public String register(RegisterRequest request) {
        // 1️⃣ Kiểm tra email trùng
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email đã tồn tại!";
        }

        // 2️⃣ Kiểm tra CCCD trùng
        if (userRepository.existsByCccd(request.getCccd())) {
            return "CCCD đã tồn tại!";
        }

        // 3️⃣ Lưu file ảnh (nếu có)
        String cccdFrontUrl = saveFile(request.getCccdFront(), "cccd_front");
        String cccdBackUrl = saveFile(request.getCccdBack(), "cccd_back");
        String driverLicenseUrl = saveFile(request.getDriverLicenseImg(), "driver_license");

        // 4️⃣ Tạo đối tượng User
        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(request.getPassword());
        newUser.setCccd(request.getCccd());
        newUser.setDriverLicense(request.getDriverLicense());
        newUser.setBirthday(request.getBirthday());
        newUser.setLocation(request.getLocation());
        newUser.setRole("user");
        newUser.setVerificationStatus("pending");
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setCccdFrontUrl(cccdFrontUrl);
        newUser.setCccdBackUrl(cccdBackUrl);
        newUser.setDriverLicenseUrl(driverLicenseUrl);

        userRepository.save(newUser);
        return "Đăng ký thành công!";
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
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
        }
    }
}