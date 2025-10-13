package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.entity.CoOwnershipGroup;
import swp302.topic6.evcoownership.entity.GroupMember;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.repository.CoOwnershipGroupRepository;
import swp302.topic6.evcoownership.repository.GroupMemberRepository;
import swp302.topic6.evcoownership.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CoOwnershipGroupRepository coOwnershipGroupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    // ==========================
    // 1️⃣ DUYỆT NHÓM CHIA SẺ XE
    // ==========================
    public List<CoOwnershipGroup> getPendingGroups() {
        return coOwnershipGroupRepository.findByApprovalStatus("pending");
    }

    public String approveGroup(Long groupId, Long adminId) {
        Optional<CoOwnershipGroup> groupOpt = coOwnershipGroupRepository.findById(groupId);
        Optional<User> adminOpt = userRepository.findById(adminId);

        if (groupOpt.isEmpty()) { return "Nhóm không tồn tại!"; }
        if (adminOpt.isEmpty()) { return "Admin không tồn tại!"; }

        CoOwnershipGroup group = groupOpt.get();
        User admin = adminOpt.get();

        group.setApprovalStatus("approved"); // ĐÃ SỬA: Tên setter
        group.setApprovedBy(admin);       // ĐÃ SỬA: Truyền đối tượng User
        coOwnershipGroupRepository.save(group);

        return "✅ Nhóm đã được duyệt thành công!";
    }

    public String rejectGroup(Long groupId, String reason, Long adminId) {
        Optional<CoOwnershipGroup> groupOpt = coOwnershipGroupRepository.findById(groupId);
        Optional<User> adminOpt = userRepository.findById(adminId);

        if (groupOpt.isEmpty()) { return "Nhóm không tồn tại!"; }
        if (adminOpt.isEmpty()) { return "Admin không tồn tại!"; }

        CoOwnershipGroup group = groupOpt.get();
        User admin = adminOpt.get();

        group.setApprovalStatus("rejected"); // ĐÃ SỬA: Tên setter
        group.setRejectReason(reason);      // ĐÃ SỬA: Tên setter
        group.setApprovedBy(admin);       // ĐÃ SỬA: Truyền đối tượng User
        coOwnershipGroupRepository.save(group);

        return "❌ Nhóm đã bị từ chối với lý do: " + reason;
    }

    // ==========================
    // 1.5️⃣ DUYỆT THÀNH VIÊN
    // ==========================

    // SỬA: findByStatus -> findByJoinStatus
    public List<GroupMember> getPendingMembers() {
        return groupMemberRepository.findByJoinStatus("pending");
    }

    public String approveMember(Long memberId) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return "Yêu cầu tham gia không tồn tại!";
        }
        GroupMember member = memberOpt.get();
        member.setJoinStatus("active"); // ĐÃ SỬA LỖI CUỐI: setStatus -> setJoinStatus
        groupMemberRepository.save(member);
        return "✅ Yêu cầu tham gia đã được duyệt!";
    }

    public String rejectMember(Long memberId, String reason) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return "Yêu cầu tham gia không tồn tại!";
        }
        GroupMember member = memberOpt.get();
        member.setJoinStatus("rejected"); // ĐÃ SỬA LỖI CUỐI: setStatus -> setJoinStatus
        groupMemberRepository.save(member);
        return "❌ Yêu cầu tham gia đã bị từ chối!";
    }


    // =========================================
    // 2️⃣ DUYỆT NGƯỜI DÙNG / XÁC MINH TÀI KHOẢN
    // =========================================
    public List<User> getPendingUsers() {
        return userRepository.findByVerificationStatus("pending");
    }

    public String verifyUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) { return "Người dùng không tồn tại!"; }

        User user = userOpt.get();
        user.setVerificationStatus("verified");
        userRepository.save(user);

        return "✅ Tài khoản " + user.getFullName() + " đã được xác minh!";
    }

    public String rejectUserVerification(Long userId, String reason) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) { return "Người dùng không tồn tại!"; }

        User user = userOpt.get();
        user.setVerificationStatus("rejected");
        userRepository.save(user);

        return "❌ Tài khoản " + user.getFullName() + " bị từ chối xác minh. Lý do: " + reason;
    }
}