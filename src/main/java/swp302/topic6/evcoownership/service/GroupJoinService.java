package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.entity.*;
import swp302.topic6.evcoownership.repository.*;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupJoinService {

    private final CoOwnershipGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    // 🟩 Người dùng gửi yêu cầu tham gia nhóm
    public String requestJoinGroup(Long groupId, Long userId) {
        Optional<CoOwnershipGroup> groupOpt = groupRepository.findById(groupId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (groupOpt.isEmpty() || userOpt.isEmpty()) {
            return "❌ Nhóm hoặc người dùng không tồn tại!";
        }

        CoOwnershipGroup group = groupOpt.get();
        User user = userOpt.get();

        // Không cho vào nhóm đã đủ hoặc không còn tuyển
        if (!"recruiting".equalsIgnoreCase(group.getStatus())) {
            return "❌ Nhóm này hiện không tuyển thêm thành viên!";
        }

        // Kiểm tra người dùng đã gửi yêu cầu hoặc đã trong nhóm chưa
        boolean alreadyJoined = groupMemberRepository.findAll().stream()
                .anyMatch(m -> m.getGroup().getGroupId().equals(groupId)
                        && m.getUser().getUserId().equals(userId));

        if (alreadyJoined) {
            return "⚠️ Bạn đã gửi yêu cầu hoặc đã là thành viên của nhóm này!";
        }

        // Tạo yêu cầu tham gia mới
        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .ownershipPercentage(0.0)
                .joinStatus("pending")
                .joinDate(new Date())
                .build();

        groupMemberRepository.save(member);
        return "✅ Gửi yêu cầu tham gia nhóm thành công!";
    }

    // 🟦 Duyệt hoặc từ chối yêu cầu tham gia
    public String reviewJoinRequest(Long memberId, boolean approved) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findById(memberId);

        if (memberOpt.isEmpty()) {
            return "❌ Không tìm thấy yêu cầu này!";
        }

        GroupMember member = memberOpt.get();
        member.setJoinStatus(approved ? "approved" : "rejected");
        groupMemberRepository.save(member);

        return approved
                ? "✅ Thành viên đã được duyệt vào nhóm!"
                : "❌ Yêu cầu tham gia đã bị từ chối!";
    }
}
