package swp302.topic6.evcoownership.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp302.topic6.evcoownership.dto.RegisterRequest;
import swp302.topic6.evcoownership.service.RegisterService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    // Đăng ký người dùng (với file upload)
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public String register(@ModelAttribute RegisterRequest request) {
        return registerService.register(request);
    }
}