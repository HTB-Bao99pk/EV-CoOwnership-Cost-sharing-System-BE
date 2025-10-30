package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.dto.CreateGroupRequest;
import swp302.topic6.evcoownership.entity.CoOwnershipGroup;
import swp302.topic6.evcoownership.entity.GroupMember;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.entity.Vehicle;
import swp302.topic6.evcoownership.repository.CoOwnershipGroupRepository;
import swp302.topic6.evcoownership.repository.GroupMemberRepository;
import swp302.topic6.evcoownership.repository.UserRepository;
import swp302.topic6.evcoownership.repository.VehicleRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final CoOwnershipGroupRepository groupRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * 🟢 Chủ xe tạo nhóm chia sẻ xe
     */
    public String createGroup(CreateGroupRequest request, Long userId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(request.getVehicleId());
        Optional<User> userOpt = userRepository.findById(userId);

        if (vehicleOpt.isEmpty() || userOpt.isEmpty()) {
            return "❌ Xe hoặc người dùng không tồn tại!";
        }

        Vehicle vehicle = vehicleOpt.get();
        User creator = userOpt.get();

        // ✅ Kiểm tra quyền sở hữu
        if (vehicle.getOwner() == null || !vehicle.getOwner().getUserId().equals(userId)) {
            return "❌ Bạn không phải chủ sở hữu xe này!";
        }

        // ✅ Kiểm tra xe đã nằm trong nhóm khác hay chưa
        boolean vehicleInOtherGroup = groupRepository.findAll().stream()
                .anyMatch(g -> g.getVehicle() != null
                        && g.getVehicle().getVehicle_id().equals(vehicle.getVehicle_id())
                        && !"rejected".equalsIgnoreCase(g.getApprovalStatus())
                        && !"closed".equalsIgnoreCase(g.getStatus()));

        if (vehicleInOtherGroup) {
            return "⚠️ Xe này đã nằm trong nhóm khác hoặc nhóm đó đang hoạt động/chờ duyệt!";
        }

        // ✅ Kiểm tra trạng thái xe
        if (!"available".equalsIgnoreCase(vehicle.getStatus())) {
            return "⚠️ Xe này hiện không sẵn sàng để tạo nhóm (đang chờ duyệt hoặc thuộc nhóm khác)!";
        }

        // ✅ Tạo nhóm chia sẻ
        CoOwnershipGroup group = CoOwnershipGroup.builder()
                .vehicle(vehicle)
                .createdBy(creator)
                .groupName(request.getGroupName())
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

        // ✅ Cập nhật trạng thái xe
        vehicle.setStatus("pending_approval");
        vehicleRepository.save(vehicle);

        // ✅ Thêm người tạo nhóm vào danh sách thành viên (chủ nhóm)
        GroupMember ownerMember = GroupMember.builder()
                .group(group)
                .user(creator)
                .ownershipPercentage(100.0) // Tạm thời 100%, sau này sẽ chia lại khi có thêm thành viên
                .joinStatus("approved")
                .joinDate(new Date())
                .build();

        groupMemberRepository.save(ownerMember);

        return "✅ Tạo nhóm thành công! Nhóm đang chờ admin duyệt.";
    }

    /**
     * 🔍 Xem chi tiết nhóm
     */
    public Optional<CoOwnershipGroup> getGroupById(Long groupId) {
        return groupRepository.findById(groupId);
    }

    /**
     * 📋 Danh sách nhóm đang tuyển thành viên
     */
    public java.util.List<CoOwnershipGroup> getRecruitingGroups() {
        return groupRepository.findByStatus("recruiting");
    }
}
