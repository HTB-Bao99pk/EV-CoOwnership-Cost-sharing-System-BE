package swp302.topic6.evcoownership.dto;

import lombok.Data;
import swp302.topic6.evcoownership.entity.User;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private User user; // Thêm trường user để trả về toàn bộ thông tin người dùng

}
