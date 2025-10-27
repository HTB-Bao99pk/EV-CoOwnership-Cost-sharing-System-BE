package fu.swp.evcs.service;

import java.util.Date;

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
import lombok.RequiredArgsConstructor;

/**
 * ✅ GroupJoinService - Xử lý logic tham gia nhóm
 * 
 * Service xử lý validation và throw exceptions
 */
@Service
@RequiredArgsConstructor
public class GroupJoinService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    /**
     * 🟩 Người dùng gửi yêu cầu tham gia nhóm
     */
    public String requestJoinGroup(Long groupId, User currentUser) {
        // 1. Validation authentication
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }
        
        // 2. Validation verification
        if (!"verified".equalsIgnoreCase(currentUser.getVerificationStatus())) {
            throw new ForbiddenException("Tài khoản chưa được xác minh!");
        }
        
        // 3. Tìm group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        // 4. Kiểm tra trạng thái nhóm
        if (!"recruiting".equalsIgnoreCase(group.getStatus())) {
            throw new BadRequestException("Nhóm này hiện không tuyển thêm thành viên!");
        }

        // 5. Kiểm tra đã join chưa
        boolean alreadyJoined = memberRepository.findAll().stream()
                .anyMatch(m -> m.getGroup().getId().equals(groupId)
                        && m.getUser().getId().equals(currentUser.getId()));

        if (alreadyJoined) {
            throw new BadRequestException("Bạn đã gửi yêu cầu hoặc đã là thành viên của nhóm này!");
        }

        // 6. Tạo yêu cầu tham gia mới
        Member member = Member.builder()
                .group(group)
                .user(currentUser)
                .ownershipPercentage(0.0)
                .joinStatus("pending")
                .joinDate(new Date())
                .build();

        memberRepository.save(member);
        return "✅ Gửi yêu cầu tham gia nhóm thành công!";
    }

    /**
     * 🟦 Duyệt hoặc từ chối yêu cầu tham gia
     */
    public String reviewJoinRequest(Long memberId, boolean approved) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu này!"));

        member.setJoinStatus(approved ? "approved" : "rejected");
        memberRepository.save(member);

        return approved
                ? "✅ Thành viên đã được duyệt vào nhóm!"
                : "❌ Yêu cầu tham gia đã bị từ chối!";
    }
}
