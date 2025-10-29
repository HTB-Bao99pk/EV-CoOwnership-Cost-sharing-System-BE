package fu.swp.evcs.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fu.swp.evcs.entity.User;
import fu.swp.evcs.exception.ForbiddenException;
import fu.swp.evcs.exception.ResourceNotFoundException;
import fu.swp.evcs.exception.UnauthorizedException;
import fu.swp.evcs.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * ✅ UserService - Xử lý tất cả logic về User
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 🟢 GET ALL: Danh sách tất cả users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 🟢 GET BY ID: Chi tiết user
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + id));
    }

    /**
     * 🟢 PUT: Cập nhật thông tin user
     */
    @Transactional
    public User updateUser(Long id, User userUpdate, User currentUser) {
        // 1. Validation authentication
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        // 2. Kiểm tra quyền (chỉ user tự cập nhật thông tin của mình hoặc admin)
        if (!currentUser.getId().equals(id) && !"ADMIN".equals(currentUser.getRole())) {
            throw new ForbiddenException("Bạn không có quyền cập nhật thông tin user này!");
        }

        // 3. Tìm user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + id));

        // 4. Cập nhật thông tin
        if (userUpdate.getFullName() != null) {
            existingUser.setFullName(userUpdate.getFullName());
        }
        if (userUpdate.getLocation() != null) {
            existingUser.setLocation(userUpdate.getLocation());
        }
        if (userUpdate.getCccd() != null) {
            existingUser.setCccd(userUpdate.getCccd());
        }
        if (userUpdate.getDriverLicense() != null) {
            existingUser.setDriverLicense(userUpdate.getDriverLicense());
        }
        if (userUpdate.getBirthday() != null) {
            existingUser.setBirthday(userUpdate.getBirthday());
        }

        return userRepository.save(existingUser);
    }

    /**
     * 🟢 DELETE: Xóa user
     */
    @Transactional
    public void deleteUser(Long id, User currentUser) {
        // 1. Validation authentication
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        // 2. Kiểm tra quyền (chỉ admin mới được xóa user)
        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new ForbiddenException("Chỉ admin mới có quyền xóa user!");
        }

        // 3. Tìm user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + id));

        // 4. Xóa user
        userRepository.delete(user);
    }
}
