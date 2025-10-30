package fu.swp.evcs.service;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Service;

import fu.swp.evcs.dto.JoinGroupRequest;
import fu.swp.evcs.dto.ReviewMemberRequest;
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

    public String requestJoinGroup(Long groupId, JoinGroupRequest request, User currentUser) {
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

        if (group.getIsLocked() != null && group.getIsLocked()) {
            throw new BadRequestException("Nhóm đã đóng, không nhận thêm thành viên!");
        }

        if (request.getProposedOwnershipPercentage() == null) {
            throw new BadRequestException("Vui lòng nhập phần trăm sở hữu mong muốn!");
        }

        double minOwnership = group.getMinOwnershipPercentage() != null 
                ? group.getMinOwnershipPercentage().doubleValue() 
                : 15.0;

        if (request.getProposedOwnershipPercentage() < minOwnership) {
            throw new BadRequestException(
                "Phần trăm sở hữu tối thiểu là " + minOwnership + "%!");
        }

        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new BadRequestException("Vui lòng nhập lý do tham gia!");
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
                .proposedOwnershipPercentage(request.getProposedOwnershipPercentage())
                .ownershipPercentage(0.0)
                .reason(request.getReason())
                .joinStatus("pending")
                .joinDate(new Date())
                .build();

        memberRepository.save(member);
        return "Gửi yêu cầu tham gia nhóm thành công!";
    }

    public String reviewMemberRequest(Long groupId, Long memberId, ReviewMemberRequest request, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        if (!group.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Chỉ chủ nhóm mới có quyền duyệt thành viên!");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Yêu cầu tham gia không tồn tại!"));

        if (!member.getGroup().getId().equals(groupId)) {
            throw new BadRequestException("Thành viên không thuộc nhóm này!");
        }

        if (!"pending".equalsIgnoreCase(member.getJoinStatus())) {
            throw new BadRequestException("Yêu cầu này đã được xử lý!");
        }

        String action = request.getAction();

        if ("approve".equalsIgnoreCase(action)) {
            BigDecimal currentTotal = group.getTotalOwnershipPercentage();
            BigDecimal proposedPercentage = BigDecimal.valueOf(member.getProposedOwnershipPercentage());
            BigDecimal newTotal = currentTotal.add(proposedPercentage);

            if (newTotal.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BadRequestException(
                    "Tổng phần trăm sở hữu vượt quá 100%! Hiện tại: " + currentTotal + "%, đề xuất: " + proposedPercentage + "%");
            }

            member.setOwnershipPercentage(member.getProposedOwnershipPercentage());
            member.setJoinStatus("approved");
            memberRepository.save(member);

            group.setTotalOwnershipPercentage(newTotal);
            
            long approvedMembersCount = memberRepository.findByGroup_Id(groupId).stream()
                .filter(m -> "approved".equalsIgnoreCase(m.getJoinStatus()))
                .count();

            if (newTotal.compareTo(BigDecimal.valueOf(100)) >= 0 || approvedMembersCount >= group.getMaxMembers()) {
                group.setIsLocked(true);
                group.setStatus("locked");
            }

            groupRepository.save(group);
            return "Đã duyệt thành viên thành công!";

        } else if ("counter_offer".equalsIgnoreCase(action)) {
            if (request.getCounterOfferPercentage() == null) {
                throw new BadRequestException("Vui lòng nhập phần trăm đề xuất lại!");
            }

            double minOwnership = group.getMinOwnershipPercentage() != null 
                    ? group.getMinOwnershipPercentage().doubleValue() 
                    : 15.0;

            if (request.getCounterOfferPercentage() < minOwnership) {
                throw new BadRequestException("Phần trăm đề xuất tối thiểu là " + minOwnership + "%!");
            }

            member.setCounterOfferPercentage(request.getCounterOfferPercentage());
            member.setCounterOfferStatus("pending_user_response");
            memberRepository.save(member);

            return "Đã gửi đề xuất lại cho thành viên: " + request.getCounterOfferPercentage() + "%";

        } else if ("reject".equalsIgnoreCase(action)) {
            member.setJoinStatus("rejected");
            memberRepository.save(member);
            return "Đã từ chối yêu cầu tham gia!";

        } else {
            throw new BadRequestException("Action không hợp lệ! Chỉ chấp nhận: approve, counter_offer, reject");
        }
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
