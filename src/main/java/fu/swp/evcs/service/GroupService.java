package fu.swp.evcs.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fu.swp.evcs.dto.CreateGroupRequest;
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
import fu.swp.evcs.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;

/**
 * ✅ GroupService - Xử lý tất cả logic về nhóm chia sẻ xe
 * 
 * Service xử lý:
 * - Validation (authentication, authorization, business rules)
 * - Business logic
 * - Throw exceptions (GlobalExceptionHandler sẽ bắt)
 */
@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final VehicleRepository vehicleRepository;
    private final MemberRepository memberRepository;

    /**
     * 🟢 Chủ xe tạo nhóm chia sẻ xe
     */
    public String createGroup(CreateGroupRequest request, User currentUser) {
        // 1. Validation authentication
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }
        
        // 2. Validation verification
        if (!"verified".equalsIgnoreCase(currentUser.getVerificationStatus())) {
            throw new ForbiddenException("Tài khoản chưa được xác minh!");
        }
        
        // 3. Tìm vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Xe không tồn tại!"));

        // 4. Kiểm tra quyền sở hữu
        if (vehicle.getOwner() == null || !vehicle.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không phải chủ sở hữu xe này!");
        }

        // 5. Kiểm tra xe đã nằm trong nhóm khác hay chưa
        boolean vehicleInOtherGroup = groupRepository.findAll().stream()
                .anyMatch(g -> g.getVehicle() != null
                        && g.getVehicle().getId().equals(vehicle.getId())
                        && !"rejected".equalsIgnoreCase(g.getApprovalStatus())
                        && !"closed".equalsIgnoreCase(g.getStatus()));

        if (vehicleInOtherGroup) {
            throw new BadRequestException("Xe này đã nằm trong nhóm khác hoặc nhóm đó đang hoạt động/chờ duyệt!");
        }

        // 6. Kiểm tra trạng thái xe
        if (!"available".equalsIgnoreCase(vehicle.getStatus())) {
            throw new BadRequestException("Xe này hiện không sẵn sàng để tạo nhóm!");
        }

        // 7. Tạo nhóm chia sẻ
        Group group = Group.builder()
                .vehicle(vehicle)
                .createdBy(currentUser)
                .name(request.getGroupName())
                .description(request.getDescription())
                .estimatedValue(request.getEstimatedValue())
                .status("recruiting")
                .approvalStatus("pending")
                .maxMembers(request.getMaxMembers() != null ? request.getMaxMembers() : 5)
                .minOwnershipPercentage(request.getMinOwnershipPercentage() != null
                        ? BigDecimal.valueOf(request.getMinOwnershipPercentage())
                        : BigDecimal.valueOf(10.0))
                .createdAt(new Date())
                .build();

        groupRepository.save(group);

        // 8. Cập nhật trạng thái xe
        vehicle.setStatus("pending_approval");
        vehicleRepository.save(vehicle);

        // 9. Thêm người tạo nhóm vào danh sách thành viên (chủ nhóm)
        Member ownerMember = Member.builder()
                .group(group)
                .user(currentUser)
                .ownershipPercentage(100.0)
                .joinStatus("approved")
                .joinDate(new Date())
                .build();

        memberRepository.save(ownerMember);

        return "✅ Tạo nhóm thành công! Nhóm đang chờ admin duyệt.";
    }

    /**
     * 🔍 Xem chi tiết nhóm
     */
    public Optional<Group> getGroupById(Long groupId) {
        return groupRepository.findById(groupId);
    }

    /**
     * 📋 Danh sách nhóm đang tuyển thành viên
     */
    public java.util.List<Group> getRecruitingGroups() {
        return groupRepository.findByStatus("recruiting");
    }
}
