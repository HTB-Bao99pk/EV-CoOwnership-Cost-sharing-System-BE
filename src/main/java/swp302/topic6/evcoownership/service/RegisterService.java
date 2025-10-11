package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.dto.RegisterRequest;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.repository.UserRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;

    public String register(RegisterRequest request) {
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email đã tồn tại!";
        }

        //Kiểm tra CCCD đã tồn tại
        if (userRepository.existsByCccd(request.getCccd())) {
            return "CCCD đã tồn tại!";
        }

        // Tạo user mới
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
        userRepository.save(newUser);
        return "success";
    }
    private String saveBase64Image(String base64Data, String prefix) {
        if (base64Data == null || base64Data.isEmpty()) return null;

        try {
            // Tạo thư mục nếu chưa có
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);

            // Loại bỏ tiền tố "data:image/png;base64," nếu có
            String base64Image = base64Data.contains(",") ?
                    base64Data.split(",")[1] : base64Data;

            // Giải mã base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Tạo tên file duy nhất
            String fileName = prefix + "_" + System.currentTimeMillis() + ".png";
            Path filePath = uploadDir.resolve(fileName);

            // Ghi file ra ổ đĩa
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(imageBytes);
            }

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu ảnh: " + e.getMessage());
        }
    }
}
