package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.dto.LoginRequest;
import swp302.topic6.evcoownership.dto.LoginResponse;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        System.out.println("===> Request: " + request);

        LoginResponse res = new LoginResponse();

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        System.out.println("===> User from DB: " + user);

        if (user == null) {
            res.setSuccess(false);
            res.setMessage("Tài khoản không tồn tại");
            return res;
        }

        if (!user.getPasswordHash().equals(request.getPassword())) {
            res.setSuccess(false);
            res.setMessage("Sai mật khẩu");
            return res;
        }

        res.setSuccess(true);
        res.setMessage("Đăng nhập thành công!");
        res.setUserId(user.getUserId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole());
        res.setUser(user);
        return res;
    }

}
