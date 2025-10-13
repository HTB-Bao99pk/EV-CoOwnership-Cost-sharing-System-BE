package swp302.topic6.evcoownership.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import swp302.topic6.evcoownership.entity.CoOwnershipGroup;
import swp302.topic6.evcoownership.entity.GroupMember;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.repository.CoOwnershipGroupRepository;
import swp302.topic6.evcoownership.repository.GroupMemberRepository;
import swp302.topic6.evcoownership.repository.UserRepository;

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
        // Kiểm tra theo cấu hình của nhóm
        Long groupId = member.getGroup().getGroupId();
        Optional<CoOwnershipGroup> groupOpt = coOwnershipGroupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return "Nhóm không tồn tại!";
        }
        CoOwnershipGroup group = groupOpt.get();

        int activeCount = groupMemberRepository.countByGroup_GroupIdAndJoinStatus(groupId, "active");
    Integer maxMembersObj = group.getMaxMembers();
    int maxMembers = 5;
    if (maxMembersObj != null) maxMembers = maxMembersObj;
        if (activeCount >= maxMembers) {
            return "Nhóm đã đạt giới hạn " + maxMembers + " thành viên. Không thể duyệt thêm.";
        }

        // Kiểm tra tỷ lệ đóng góp tối thiểu theo nhóm
        Double requestedPct = member.getOwnershipPercentage();
    double minPct = group.getMinOwnershipPercentage() != null ? group.getMinOwnershipPercentage().doubleValue() : 10.0;
        if (requestedPct == null || requestedPct < minPct) {
            return "Tỷ lệ đóng góp tối thiểu để tham gia là " + minPct + "%";
        }

        // Kiểm tra tổng tỷ lệ ownership hiện tại + requested không vượt quá 100%
        java.util.List<GroupMember> activeMembers = groupMemberRepository.findByGroup_GroupIdAndJoinStatus(groupId, "active");
        double total = 0.0;
        for (GroupMember gm : activeMembers) {
            if (gm.getOwnershipPercentage() != null) total += gm.getOwnershipPercentage();
        }
        if (total + requestedPct > 100.0) {
            return "Không thể duyệt: tổng tỷ lệ ownership sẽ vượt quá 100%. Hiện tại: " + total + "%";
        }

        member.setJoinStatus("active"); // ĐÃ SỬA LỖI CUỐI: setStatus -> setJoinStatus
        groupMemberRepository.save(member);
        // Sau khi duyệt, tự động điều chỉnh ownership của tất cả thành viên active (chia đều)
        java.util.List<GroupMember> updatedActiveMembers = groupMemberRepository.findByGroup_GroupIdAndJoinStatus(groupId, "active");
        int n = updatedActiveMembers.size();
        if (n > 0) {
            BigDecimal totalBd = new BigDecimal("100.00");
            BigDecimal share = totalBd.divide(new BigDecimal(n), 2, RoundingMode.DOWN);
            BigDecimal sum = share.multiply(new BigDecimal(n));
            BigDecimal diff = totalBd.subtract(sum); // remainder (<= 0.99)

            for (int i = 0; i < updatedActiveMembers.size(); i++) {
                GroupMember gm = updatedActiveMembers.get(i);
                BigDecimal newPct = share;
                if (i == 0) {
                    // add remainder to the first member to ensure total sums to 100
                    newPct = newPct.add(diff);
                }
                gm.setOwnershipPercentage(newPct.doubleValue());
                groupMemberRepository.save(gm);
            }
        }
        return "✅ Yêu cầu tham gia đã được duyệt!";
    }

    /**
     * Admin cập nhật cấu hình nhóm: maxMembers và minOwnershipPercentage
     */
    public String updateGroupSettings(Long groupId, Integer maxMembers, BigDecimal minOwnershipPercentage) {
        Optional<CoOwnershipGroup> groupOpt = coOwnershipGroupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            return "Nhóm không tồn tại!";
        }
        CoOwnershipGroup group = groupOpt.get();
        if (maxMembers != null) {
            group.setMaxMembers(maxMembers);
        }
        if (minOwnershipPercentage != null) {
            group.setMinOwnershipPercentage(minOwnershipPercentage);
        }
        coOwnershipGroupRepository.save(group);
        return "Cập nhật cấu hình nhóm thành công";
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