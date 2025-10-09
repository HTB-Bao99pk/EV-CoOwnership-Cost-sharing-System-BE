package swp302.topic6.evcoownership.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.dto.RegisterRequest;
import swp302.topic6.evcoownership.service.RegisterService;

@RestController
@RequestMapping("/api/auth") // đường dẫn chung
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    // API đăng ký người dùng
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return registerService.register(request);
    }
}
