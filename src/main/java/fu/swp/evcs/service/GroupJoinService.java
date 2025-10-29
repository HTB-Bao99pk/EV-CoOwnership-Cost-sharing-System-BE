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
 * GroupJoinService - Handles group joining logic
 *
 * Service handles validation and throws exceptions
 */
@Service
@RequiredArgsConstructor
public class GroupJoinService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    public String requestJoinGroup(Long groupId, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }
        
        if (!"verified".equalsIgnoreCase(currentUser.getVerificationStatus())) {
            throw new ForbiddenException("Tài khoản chưa được xác minh!");
        }
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        if (!"recruiting".equalsIgnoreCase(group.getStatus())) {
            throw new BadRequestException("Nhóm này hiện không tuyển thêm thành viên!");
        }

        boolean alreadyJoined = memberRepository.findAll().stream()
                .anyMatch(m -> m.getGroup().getId().equals(groupId)
                        && m.getUser().getId().equals(currentUser.getId()));

        if (alreadyJoined) {
            throw new BadRequestException("Bạn đã gửi yêu cầu hoặc đã là thành viên của nhóm này!");
        }

        Member member = Member.builder()
                .group(group)
                .user(currentUser)
                .ownershipPercentage(0.0)
                .joinStatus("pending")
                .joinDate(new Date())
                .build();

        memberRepository.save(member);
        return "Gửi yêu cầu tham gia nhóm thành công!";
    }

    public String reviewJoinRequest(Long memberId, boolean approved) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu này!"));

        member.setJoinStatus(approved ? "approved" : "rejected");
        memberRepository.save(member);

        return approved
                ? "Thành viên đã được duyệt vào nhóm!"
                : "Yêu cầu tham gia đã bị từ chối!";
    }
}
