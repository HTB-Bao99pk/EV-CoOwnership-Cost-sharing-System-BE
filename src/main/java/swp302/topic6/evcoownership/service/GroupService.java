package swp302.topic6.evcoownership.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import swp302.topic6.evcoownership.dto.CreateGroupRequest;
import swp302.topic6.evcoownership.dto.GroupDetailResponse;
import swp302.topic6.evcoownership.entity.CoOwnershipGroup;
import swp302.topic6.evcoownership.entity.GroupMember;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.entity.Vehicle;
import swp302.topic6.evcoownership.repository.CoOwnershipGroupRepository;
import swp302.topic6.evcoownership.repository.UserRepository;
import swp302.topic6.evcoownership.repository.VehicleRepository;


@Service
@RequiredArgsConstructor
public class GroupService {

    private final CoOwnershipGroupRepository groupRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final swp302.topic6.evcoownership.repository.GroupMemberRepository groupMemberRepository;

    // Trả về số lượng thành viên active trong nhóm
    public int countActiveMembers(Long groupId) {
        return groupMemberRepository.countByGroup_GroupIdAndJoinStatus(groupId, "active");
    }

    // User requests to join a group with a requested ownership percentage
    public String requestToJoinGroup(Long groupId, Long userId, Double requestedPercentage) {
        // check group exists
        Optional<CoOwnershipGroup> groupOpt = groupRepository.findById(groupId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (groupOpt.isEmpty() || userOpt.isEmpty()) {
            return "Nhóm hoặc người dùng không tồn tại";
        }

        CoOwnershipGroup group = groupOpt.get();

        // validate minimum percentage based on group setting
        double minPct = group.getMinOwnershipPercentage() != null ? group.getMinOwnershipPercentage().doubleValue() : 10.0;
        if (requestedPercentage == null || requestedPercentage < minPct) {
            return "Tỷ lệ đóng góp tối thiểu để tham gia là " + minPct + "%";
        }
        User user = userOpt.get();

        // create pending GroupMember
        GroupMember req = GroupMember.builder()
                .group(group)
                .user(user)
                .ownershipPercentage(requestedPercentage)
                .joinStatus("pending")
                .joinDate(new Date())
                .build();

        groupMemberRepository.save(req);
        return "Yêu cầu tham gia đã được gửi (chờ admin duyệt).";
    }

    /**
     * Trả về chi tiết nhóm kèm số lượng thành viên active (memberCount)
     */
    public GroupDetailResponse getGroupDetail(Long groupId) {
        Optional<CoOwnershipGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) return null;
        CoOwnershipGroup g = groupOpt.get();
        GroupDetailResponse resp = new GroupDetailResponse();
        resp.setGroupId(g.getGroupId());
    resp.setVehicleId(g.getVehicle() != null ? g.getVehicle().getVehicle_id() : null);
        resp.setCreatedByUserId(g.getCreatedBy() != null ? g.getCreatedBy().getUserId() : null);
        resp.setGroupName(g.getGroupName());
        resp.setDescription(g.getDescription());
        resp.setStatus(g.getStatus());
        resp.setApprovalStatus(g.getApprovalStatus());
        resp.setCreatedAt(g.getCreatedAt());
        resp.setMaxMembers(g.getMaxMembers());
        resp.setMinOwnershipPercentage(g.getMinOwnershipPercentage());
        int memberCount = countActiveMembers(groupId);
        resp.setMemberCount(memberCount);
    // members list intentionally omitted — frontend only requires memberCount
        return resp;
    }

    public String createGroup(CreateGroupRequest request, Long userId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(request.getVehicleId());
        Optional<User> userOpt = userRepository.findById(userId);

        if (vehicleOpt.isEmpty() || userOpt.isEmpty()) {
            return "Xe hoặc người dùng không tồn tại!";
        }

        Vehicle vehicle = vehicleOpt.get();
        User creator = userOpt.get();

        // ❌ Nếu không phải chủ xe → không được phép tạo nhóm
        if (vehicle.getOwner() == null || !vehicle.getOwner().getUserId().equals(userId)) {
            return "Bạn không phải chủ sở hữu xe này!";
        }

        // ❌ Nếu xe đang thuộc nhóm khác → chặn
        if (!"available".equalsIgnoreCase(vehicle.getStatus())) {
            return "Xe này hiện đang thuộc nhóm khác hoặc đang chờ duyệt!";
        }

        // ✅ Tạo nhóm chia sẻ xe
        CoOwnershipGroup group = CoOwnershipGroup.builder()
                .vehicle(vehicle)
                .createdBy(creator) // ĐÃ SỬA: created_by -> createdBy
                .groupName(request.getGroupName()) // ĐÃ SỬA: group_name -> groupName
                .description(request.getDescription())
                .estimatedValue(request.getEstimatedValue()) // ĐÃ SỬA: estimated_value -> estimatedValue
                .status("recruiting")
                .approvalStatus("pending")  // ĐÃ SỬA: approval_status -> approvalStatus
                .createdAt(new Date()) // Giả định trường này là 'createdAt'
                .build();

        groupRepository.save(group);

        // Thêm người tạo nhóm làm thành viên đầu tiên (active)
        GroupMember creatorMember = GroupMember.builder()
                .group(group)
                .user(creator)
                .ownershipPercentage(100.0)
                .joinStatus("active")
                .joinDate(new Date())
                .build();
        // Lưu member (người tạo nhóm)
        groupMemberRepository.save(creatorMember);

        // ✅ Cập nhật trạng thái xe sang "pending_approval"
        vehicle.setStatus("pending_approval");
        vehicleRepository.save(vehicle);

        return "Tạo nhóm thành công! Đang chờ admin duyệt.";
    }
}