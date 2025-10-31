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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // NEW: Hỗ trợ GET /api/users?verificationStatus=pending (Lọc theo Query Param)
    // Cần phương thức findByVerificationStatus(String status) trong UserRepository
    public List<User> getUsersByVerificationStatus(String status) {
        return userRepository.findByVerificationStatus(status);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + id));
    }

    @Transactional
    public User updateUser(Long id, User userUpdate, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        if (!currentUser.getId().equals(id) && !"ADMIN".equals(currentUser.getRole())) {
            throw new ForbiddenException("Bạn không có quyền cập nhật thông tin user này!");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + id));

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

    @Transactional
    public void deleteUser(Long id, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new ForbiddenException("Chỉ admin mới có quyền xóa user!");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + id));

        userRepository.delete(user);
    }

    // NEW: Hỗ trợ PATCH /api/users/{id}/verification (Logic xác minh Admin)
    @Transactional
    public String handleUserVerification(Long userId, boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));

        if (approved) {
            user.setVerificationStatus("verified");
            // user.setRejectReason(null); <--- BỎ DÒNG NÀY
            userRepository.save(user);
            return "Tài khoản " + user.getFullName() + " đã được xác minh!";
        } else {
            user.setVerificationStatus("rejected");
            // user.setRejectReason("..."); <--- BỎ DÒNG NÀY
            userRepository.save(user);
            return "Tài khoản " + user.getFullName() + " bị từ chối xác minh.";
        }
    }
}