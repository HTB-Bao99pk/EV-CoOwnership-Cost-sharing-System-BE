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

/**
 * ✅ MemberService - Xử lý tất cả logic về Member
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 🟢 GET ALL: Danh sách tất cả members
     */
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    /**
     * 🟢 GET BY ID: Chi tiết member
     */
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member không tồn tại với ID: " + id));
    }

    /**
     * 🟢 GET BY GROUP: Danh sách members theo group
     */
    public List<Member> getMembersByGroup(Long groupId) {
        return memberRepository.findByGroup_Id(groupId);
    }

    /**
     * 🟢 POST: Thêm member vào group
     */
    @Transactional
    public Member addMember(Member member, User currentUser) {
        // 1. Validation authentication
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        // 2. Lưu member
        return memberRepository.save(member);
    }

    /**
     * 🟢 PUT: Cập nhật member
     */
    @Transactional
    public Member updateMember(Long id, Member memberUpdate, User currentUser) {
        // 1. Validation authentication
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        // 2. Tìm member
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member không tồn tại với ID: " + id));

        // 3. Cập nhật thông tin
        if (memberUpdate.getOwnershipPercentage() != null) {
            existingMember.setOwnershipPercentage(memberUpdate.getOwnershipPercentage());
        }
        if (memberUpdate.getJoinStatus() != null) {
            existingMember.setJoinStatus(memberUpdate.getJoinStatus());
        }

        return memberRepository.save(existingMember);
    }

    /**
     * 🟢 DELETE: Xóa member
     */
    @Transactional
    public void deleteMember(Long id, User currentUser) {
        // 1. Validation authentication
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        // 2. Tìm member
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
