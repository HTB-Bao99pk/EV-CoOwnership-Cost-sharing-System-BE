package fu.swp.evcs.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fu.swp.evcs.entity.Group;
import fu.swp.evcs.entity.Member;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.exception.ForbiddenException;
import fu.swp.evcs.exception.ResourceNotFoundException;
import fu.swp.evcs.exception.UnauthorizedException;
import fu.swp.evcs.repository.GroupRepository;
import fu.swp.evcs.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member không tồn tại với ID: " + id));
    }

    public List<Member> getMembersByGroup(Long groupId) {
        return memberRepository.findByGroup_Id(groupId);
    }

    public List<Member> getMembersByGroupFiltered(Long groupId, String joinStatus, String role, String search) {
        List<Member> members = memberRepository.findByGroup_Id(groupId);

        // Filter by joinStatus
        if (joinStatus != null && !joinStatus.isBlank()) {
            members = members.stream()
                    .filter(m -> joinStatus.equalsIgnoreCase(m.getJoinStatus()))
                    .toList();
        }

        // Filter by role (owner/member)
        if (role != null && !role.isBlank()) {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

            if ("owner".equalsIgnoreCase(role)) {
                members = members.stream()
                        .filter(m -> m.getUser().getId().equals(group.getCreatedBy().getId()))
                        .toList();
            } else if ("member".equalsIgnoreCase(role)) {
                members = members.stream()
                        .filter(m -> !m.getUser().getId().equals(group.getCreatedBy().getId()))
                        .toList();
            }
        }

        // Search by user name or email
        if (search != null && !search.isBlank()) {
            String searchLower = search.toLowerCase();
            members = members.stream()
                    .filter(m -> m.getUser().getFullName().toLowerCase().contains(searchLower)
                            || m.getUser().getEmail().toLowerCase().contains(searchLower))
                    .toList();
        }

        return members;
    }

    @Transactional
    public Member addMember(Member member, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMember(Long id, Member memberUpdate, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member không tồn tại với ID: " + id));

        if (memberUpdate.getOwnershipPercentage() != null) {
            existingMember.setOwnershipPercentage(memberUpdate.getOwnershipPercentage());
        }
        if (memberUpdate.getJoinStatus() != null) {
            existingMember.setJoinStatus(memberUpdate.getJoinStatus());
        }

        return memberRepository.save(existingMember);
    }

    @Transactional
    public void deleteMember(Long id, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member không tồn tại với ID: " + id));

        // Kiểm tra quyền (chỉ chủ nhóm hoặc admin mới được xóa member)
        if (!"ADMIN".equals(currentUser.getRole()) &&
            !member.getGroup().getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không có quyền xóa member này!");
        }

        // Return ownership to available pool if member was approved
        if ("approved".equalsIgnoreCase(member.getJoinStatus()) && member.getOwnershipPercentage() != null) {
            Group group = member.getGroup();
            BigDecimal currentTotal = group.getTotalOwnershipPercentage();
            BigDecimal memberOwnership = BigDecimal.valueOf(member.getOwnershipPercentage());
            BigDecimal newTotal = currentTotal.subtract(memberOwnership);

            group.setTotalOwnershipPercentage(newTotal);

            // NOTE: Do NOT unlock group once it's locked (per owner requirement)
            // Group stays locked even if ownership drops below 100%

            groupRepository.save(group);
        }

        // Xóa member
        memberRepository.delete(member);
    }
}

