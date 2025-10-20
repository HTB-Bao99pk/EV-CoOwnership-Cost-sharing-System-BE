package swp302.topic6.evcoownership.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;

    // ⭐️ TỐI ƯU: Chỉ cần trả về UserDto là đủ,
    // không cần các trường (userId, fullName, email, role) lặp lại
    // private Long userId;
    // private String fullName;
    // private String email;
    // private String role;

    private UserDto user; // safer user DTO without sensitive fields
}