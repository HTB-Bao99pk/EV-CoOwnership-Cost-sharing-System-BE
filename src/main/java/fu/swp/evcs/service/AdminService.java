package fu.swp.evcs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fu.swp.evcs.entity.Group;
import fu.swp.evcs.entity.Member;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.entity.Vehicle;
import fu.swp.evcs.exception.BadRequestException;
import fu.swp.evcs.exception.ForbiddenException;
import fu.swp.evcs.exception.ResourceNotFoundException;
import fu.swp.evcs.exception.UnauthorizedException;
import fu.swp.evcs.repository.GroupRepository;
import fu.swp.evcs.repository.MemberRepository;
import fu.swp.evcs.repository.UserRepository;
import fu.swp.evcs.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final VehicleRepository vehicleRepository;

    public List<Group> getPendingGroups() {
        return groupRepository.findByApprovalStatus("pending");
    }

    public String handleGroupApproval(Long groupId, boolean approved, String reason, User currentAdmin) {
        if (currentAdmin == null) {
            throw new UnauthorizedException("Chưa đăng nhập");
        }
        
        if (!"admin".equalsIgnoreCase(currentAdmin.getRole())) {
            throw new ForbiddenException("Không phải admin");
        }
        
        // 3. Tìm group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));
        
        if (approved) {
            group.setApprovalStatus("approved");
            group.setApprovedBy(currentAdmin);
            groupRepository.save(group);
            return "Nhóm đã được duyệt thành công!";
        } else {
            if (reason == null || reason.trim().isEmpty()) {
                throw new BadRequestException("Lý do từ chối không được để trống!");
            }
            group.setApprovalStatus("rejected");
            group.setRejectReason(reason);
            group.setApprovedBy(currentAdmin);
            groupRepository.save(group);
            return "Nhóm đã bị từ chối với lý do: " + reason;
        }
    }

    public List<Member> getPendingMembers() {
        return memberRepository.findByJoinStatus("pending");
    }

    public String handleMemberApproval(Long memberId, boolean approved, String reason) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Yêu cầu tham gia không tồn tại!"));
        
        if (approved) {
            member.setJoinStatus("active");
            memberRepository.save(member);
            return "Yêu cầu tham gia đã được duyệt!";
        } else {
            if (reason == null || reason.trim().isEmpty()) {
                throw new BadRequestException("Lý do từ chối không được để trống!");
            }
            member.setJoinStatus("rejected");
            memberRepository.save(member);
            return "Yêu cầu tham gia đã bị từ chối!";
        }
    }

    public List<User> getPendingUsers() {
        return userRepository.findByVerificationStatus("pending");
    }

    public String handleUserVerification(Long userId, boolean approved, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));
        
        if (approved) {
            user.setVerificationStatus("verified");
            userRepository.save(user);
            return "Tài khoản " + user.getFullName() + " đã được xác minh!";
        } else {
            if (reason == null || reason.trim().isEmpty()) {
                throw new BadRequestException("Lý do từ chối xác minh không được để trống!");
            }
            user.setVerificationStatus("rejected");
            userRepository.save(user);
            return "Tài khoản " + user.getFullName() + " bị từ chối xác minh. Lý do: " + reason;
        }
    }

    public String approveGroup(Long groupId, Long adminId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin không tồn tại!"));
        
        group.setApprovalStatus("approved");
        group.setApprovedBy(admin);
        groupRepository.save(group);
        return "Nhóm đã được duyệt thành công!";
    }

    public String rejectGroup(Long groupId, String reason, Long adminId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin không tồn tại!"));
        
        group.setApprovalStatus("rejected");
        group.setRejectReason(reason);
        group.setApprovedBy(admin);
        groupRepository.save(group);
        return "Nhóm đã bị từ chối với lý do: " + reason;
    }

    public String approveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Yêu cầu tham gia không tồn tại!"));
        member.setJoinStatus("active");
        memberRepository.save(member);
        return "Yêu cầu tham gia đã được duyệt!";
    }

    public String rejectMember(Long memberId, String reason) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Yêu cầu tham gia không tồn tại!"));
        member.setJoinStatus("rejected");
        memberRepository.save(member);
        return "Yêu cầu tham gia đã bị từ chối!";
    }

    public String verifyUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));
        user.setVerificationStatus("verified");
        userRepository.save(user);
        return "Tài khoản " + user.getFullName() + " đã được xác minh!";
    }

    public String rejectUserVerification(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));
        user.setVerificationStatus("rejected");
        userRepository.save(user);
        return "Tài khoản " + user.getFullName() + " bị từ chối xác minh. Lý do: " + reason;
    }

    public List<Vehicle> getPendingVehicles() {
        return vehicleRepository.findByVerificationStatus("pending");
    }

    public String handleVehicleApproval(Long vehicleId, boolean approved, String reason, User currentAdmin) {
        if (currentAdmin == null) {
            throw new UnauthorizedException("Chưa đăng nhập");
        }

        if (!"admin".equalsIgnoreCase(currentAdmin.getRole())) {
            throw new ForbiddenException("Không phải admin");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Xe không tồn tại!"));

        if (approved) {
            vehicle.setVerificationStatus("approved");
            vehicle.setStatus("available");
            vehicle.setVerifiedBy(currentAdmin);
            vehicle.setVerifiedAt(java.time.LocalDateTime.now());
            vehicleRepository.save(vehicle);
            return "Xe đã được duyệt thành công!";
        } else {
            if (reason == null || reason.trim().isEmpty()) {
                throw new BadRequestException("Lý do từ chối không được để trống!");
            }
            vehicle.setVerificationStatus("rejected");
            vehicle.setStatus("rejected");
            vehicle.setRejectReason(reason);
            vehicle.setVerifiedBy(currentAdmin);
            vehicle.setVerifiedAt(java.time.LocalDateTime.now());
            vehicleRepository.save(vehicle);
            return "Xe đã bị từ chối với lý do: " + reason;
        }
    }
}