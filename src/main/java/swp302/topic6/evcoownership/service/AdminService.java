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

    // List all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by id
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // Approve (activate) user
    public String approveUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return "Người dùng không tồn tại!";
        User user = userOpt.get();
        user.setVerificationStatus("verified");
        userRepository.save(user);
        return "✅ Người dùng đã được bật (verified).";
    }

    // Reject user
    public String rejectUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return "Người dùng không tồn tại!";
        User user = userOpt.get();
        user.setVerificationStatus("rejected");
        userRepository.save(user);
        return "❌ Người dùng đã bị từ chối.";
    }

    // Delete user
    public String deleteUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return "Người dùng không tồn tại!";
        userRepository.deleteById(userId);
        return "🗑️ Người dùng đã bị xóa.";
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

    // ==========================
    // QUẢN LÝ YÊU CẦU THAM GIA NHÓM
    // ==========================

    /**
     * Lấy tất cả yêu cầu tham gia nhóm
     */
    public List<swp302.topic6.evcoownership.dto.AdminRequestResponse> getAllJoinRequests() {
        List<GroupMember> pendingMembers = groupMemberRepository.findByJoinStatus("pending");
        
        return pendingMembers.stream().map(member -> {
            CoOwnershipGroup group = member.getGroup();
            User user = member.getUser();
            
            return swp302.topic6.evcoownership.dto.AdminRequestResponse.builder()
                    .id(member.getMemberId())
                    .groupId(group.getGroupId())
                    .groupName(group.getGroupName())
                    .userId(user.getUserId())
                    .userName(user.getFullName())
                    .userEmail(user.getEmail())
                    .message("Yêu cầu tham gia nhóm") // Có thể thêm field message vào GroupMember entity
                    .status(member.getJoinStatus())
                    .createdAt(member.getJoinDate())
                    .updatedAt(member.getJoinDate())
                    .requestedPercentage(member.getOwnershipPercentage())
                    .build();
        }).toList();
    }

    /**
     * Lấy chi tiết yêu cầu tham gia theo ID
     */
    public Optional<swp302.topic6.evcoownership.dto.AdminRequestResponse> getJoinRequestById(Long requestId) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findById(requestId);
        
        if (memberOpt.isEmpty()) {
            return Optional.empty();
        }
        
        GroupMember member = memberOpt.get();
        CoOwnershipGroup group = member.getGroup();
        User user = member.getUser();
        
        swp302.topic6.evcoownership.dto.AdminRequestResponse response = 
            swp302.topic6.evcoownership.dto.AdminRequestResponse.builder()
                .id(member.getMemberId())
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .userId(user.getUserId())
                .userName(user.getFullName())
                .userEmail(user.getEmail())
                .message("Yêu cầu tham gia nhóm")
                .status(member.getJoinStatus())
                .createdAt(member.getJoinDate())
                .updatedAt(member.getJoinDate())
                .requestedPercentage(member.getOwnershipPercentage())
                .build();
                
        return Optional.of(response);
    }

    /**
     * Chấp nhận yêu cầu tham gia nhóm
     */
    public String acceptJoinRequest(Long requestId) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findById(requestId);
        
        if (memberOpt.isEmpty()) {
            throw new RuntimeException("Yêu cầu không tồn tại!");
        }
        
        GroupMember member = memberOpt.get();
        
        if (!"pending".equals(member.getJoinStatus())) {
            throw new RuntimeException("Yêu cầu đã được xử lý trước đó!");
        }
        
        // Kiểm tra xem nhóm có còn chỗ không
        CoOwnershipGroup group = member.getGroup();
        int currentMemberCount = groupMemberRepository.countByGroupAndJoinStatus(group, "approved");
        
        if (currentMemberCount >= group.getMaxMembers()) {
            throw new RuntimeException("Nhóm đã đầy thành viên!");
        }
        
        // Chấp nhận yêu cầu
        member.setJoinStatus("approved");
        groupMemberRepository.save(member);
        
        return "✅ Đã chấp nhận yêu cầu tham gia nhóm của " + member.getUser().getFullName();
    }

    /**
     * Từ chối yêu cầu tham gia nhóm
     */
    public String cancelJoinRequest(Long requestId, String reason) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findById(requestId);
        
        if (memberOpt.isEmpty()) {
            throw new RuntimeException("Yêu cầu không tồn tại!");
        }
        
        GroupMember member = memberOpt.get();
        
        if (!"pending".equals(member.getJoinStatus())) {
            throw new RuntimeException("Yêu cầu đã được xử lý trước đó!");
        }
        
        // Từ chối yêu cầu
        member.setJoinStatus("rejected");
        groupMemberRepository.save(member);
        
        String message = "❌ Đã từ chối yêu cầu tham gia nhóm của " + member.getUser().getFullName();
        if (reason != null && !reason.trim().isEmpty()) {
            message += ". Lý do: " + reason;
        }
        
        return message;
    }
}