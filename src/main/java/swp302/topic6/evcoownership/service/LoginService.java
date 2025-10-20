package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.dto.LoginRequest;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public User login(LoginRequest request) {

        // ⭐️ TỐI ƯU: Dùng orElseThrow để ném lỗi nếu không tìm thấy
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        // Kiểm tra mật khẩu (giữ nguyên theo yêu cầu)
        if (!user.getPasswordHash().equals(request.getPassword())) {
            // ⭐️ TỐI ƯU: Ném exception
            throw new RuntimeException("Sai mật khẩu!");
        }

        // Nếu đăng nhập thành công -> trả về user để controller lưu session
        return user;
    }
}