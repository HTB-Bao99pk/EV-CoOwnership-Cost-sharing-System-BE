package swp302.topic6.evcoownership.utils;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionUtils {

    // Lưu user sau khi đăng nhập thành công
    public void saveUserSession(HttpSession session, Long userId, String email, String fullName, String role) {
        session.setAttribute("userId", userId);
        session.setAttribute("email", email);
        session.setAttribute("fullName", fullName);
        session.setAttribute("role", role);
    }

    // Lấy ID của user hiện tại
    public Long getUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    // Lấy email
    public String getEmail(HttpSession session) {
        return (String) session.getAttribute("email");
    }

    // Kiểm tra có đăng nhập chưa
    public boolean isLoggedIn(HttpSession session) {
        return getUserId(session) != null;
    }
    public String getFullName(HttpSession session) {
        return (String) session.getAttribute("fullName");
    }

    public String getRole(HttpSession session) {
        return (String) session.getAttribute("role");
    }


    // Xoá session khi đăng xuất
    public void clearSession(HttpSession session) {
        session.invalidate();
    }
}

/*

@Autowired
private SessionUtils sessionUtils;

@GetMapping("/create")
public String createGroup(HttpSession session) {
    if (!sessionUtils.isLoggedIn(session)) {
        return "Vui lòng đăng nhập trước khi tạo nhóm!";
    }

    Long userId = sessionUtils.getUserId(session);
    return "Tạo nhóm cho user có ID = " + userId;
}

 */
