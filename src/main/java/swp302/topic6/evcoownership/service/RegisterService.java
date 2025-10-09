package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.dto.RegisterRequest;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.repository.UserRepository;

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
}
