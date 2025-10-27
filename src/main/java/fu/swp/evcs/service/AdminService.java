package fu.swp.evcs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fu.swp.evcs.entity.Group;
import fu.swp.evcs.entity.Member;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.exception.BadRequestException;
import fu.swp.evcs.exception.ForbiddenException;
import fu.swp.evcs.exception.ResourceNotFoundException;
import fu.swp.evcs.exception.UnauthorizedException;
import fu.swp.evcs.repository.GroupRepository;
import fu.swp.evcs.repository.MemberRepository;
import fu.swp.evcs.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * ✅ AdminService - Xử lý tất cả logic admin operations
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    // ========== DUYỆT NHÓM ==========
    
    public List<Group> getPendingGroups() {
        return groupRepository.findByApprovalStatus("pending");
    }

    /**
     * Xử lý duyệt/từ chối nhóm - TẤT CẢ LOGIC Ở ĐÂY
     */
    public String handleGroupApproval(Long groupId, boolean approved, String reason, User currentAdmin) {
        // 1. Validation authentication
        if (currentAdmin == null) {
            throw new UnauthorizedException("Chưa đăng nhập");
        }
        
        // 2. Validation authorization
        if (!"admin".equalsIgnoreCase(currentAdmin.getRole())) {
            throw new ForbiddenException("Không phải admin");
        }
        
        // 3. Tìm group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));
        
        // 4. Xử lý approve/reject
        if (approved) {
            group.setApprovalStatus("approved");
            group.setApprovedBy(currentAdmin);
            groupRepository.save(group);
            return "✅ Nhóm đã được duyệt thành công!";
        } else {
            // Validation lý do từ chối
            if (reason == null || reason.trim().isEmpty()) {
                throw new BadRequestException("Lý do từ chối không được để trống!");
            }
            group.setApprovalStatus("rejected");
            group.setRejectReason(reason);
            group.setApprovedBy(currentAdmin);
            groupRepository.save(group);
            return "❌ Nhóm đã bị từ chối với lý do: " + reason;
        }
    }

    // ========== DUYỆT THÀNH VIÊN ==========
    
    public List<Member> getPendingMembers() {
        return memberRepository.findByJoinStatus("pending");
    }

    /**
     * Xử lý duyệt/từ chối thành viên - TẤT CẢ LOGIC Ở ĐÂY
     */
    public String handleMemberApproval(Long memberId, boolean approved, String reason) {
        // 1. Tìm member
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Yêu cầu tham gia không tồn tại!"));
        
        // 2. Xử lý approve/reject
        if (approved) {
            member.setJoinStatus("active");
            memberRepository.save(member);
            return "✅ Yêu cầu tham gia đã được duyệt!";
        } else {
            // Validation lý do từ chối
            if (reason == null || reason.trim().isEmpty()) {
                throw new BadRequestException("Lý do từ chối không được để trống!");
            }
            member.setJoinStatus("rejected");
            memberRepository.save(member);
            return "❌ Yêu cầu tham gia đã bị từ chối!";
        }
    }

    // ========== DUYỆT XÁC MINH USER ==========
    
    public List<User> getPendingUsers() {
        return userRepository.findByVerificationStatus("pending");
    }

    /**
     * Xử lý xác minh/từ chối user - TẤT CẢ LOGIC Ở ĐÂY
     */
    public String handleUserVerification(Long userId, boolean approved, String reason) {
        // 1. Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));
        
        // 2. Xử lý approve/reject
        if (approved) {
            user.setVerificationStatus("verified");
            userRepository.save(user);
            return "✅ Tài khoản " + user.getFullName() + " đã được xác minh!";
        } else {
            // Validation lý do từ chối
            if (reason == null || reason.trim().isEmpty()) {
                throw new BadRequestException("Lý do từ chối xác minh không được để trống!");
            }
            user.setVerificationStatus("rejected");
            userRepository.save(user);
            return "❌ Tài khoản " + user.getFullName() + " bị từ chối xác minh. Lý do: " + reason;
        }
    }
    
    // ========== OLD METHODS (CÓ THỂ XÓA SAU) ==========
    
    public String approveGroup(Long groupId, Long adminId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin không tồn tại!"));
        
        group.setApprovalStatus("approved");
        group.setApprovedBy(admin);
        groupRepository.save(group);
        return "✅ Nhóm đã được duyệt thành công!";
    }

    public String rejectGroup(Long groupId, String reason, Long adminId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin không tồn tại!"));
        
        group.setApprovalStatus("rejected");
        group.setRejectReason(reason);
        group.setApprovedBy(admin);
        groupRepository.save(group);
        return "❌ Nhóm đã bị từ chối với lý do: " + reason;
    }

    public String approveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Yêu cầu tham gia không tồn tại!"));
        member.setJoinStatus("active");
        memberRepository.save(member);
        return "✅ Yêu cầu tham gia đã được duyệt!";
    }

    public String rejectMember(Long memberId, String reason) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Yêu cầu tham gia không tồn tại!"));
        member.setJoinStatus("rejected");
        memberRepository.save(member);
        return "❌ Yêu cầu tham gia đã bị từ chối!";
    }

    public String verifyUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));
        user.setVerificationStatus("verified");
        userRepository.save(user);
        return "✅ Tài khoản " + user.getFullName() + " đã được xác minh!";
    }

    public String rejectUserVerification(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));
        user.setVerificationStatus("rejected");
        userRepository.save(user);
        return "❌ Tài khoản " + user.getFullName() + " bị từ chối xác minh. Lý do: " + reason;
    }
}