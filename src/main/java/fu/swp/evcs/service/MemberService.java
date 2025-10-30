package fu.swp.evcs.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fu.swp.evcs.entity.Member;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.exception.ForbiddenException;
import fu.swp.evcs.exception.ResourceNotFoundException;
import fu.swp.evcs.exception.UnauthorizedException;
import fu.swp.evcs.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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

        // 3. Kiểm tra quyền (chỉ chủ nhóm hoặc admin mới được xóa member)
        if (!"ADMIN".equals(currentUser.getRole()) && 
            !member.getGroup().getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không có quyền xóa member này!");
        }

        // 4. Xóa member
        memberRepository.delete(member);
    }
}
