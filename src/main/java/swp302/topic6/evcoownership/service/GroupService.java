package swp302.topic6.evcoownership.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import swp302.topic6.evcoownership.dto.CreateGroupRequest;
import swp302.topic6.evcoownership.dto.EditGroupRequest;
import swp302.topic6.evcoownership.dto.GroupDetailResponse;
import swp302.topic6.evcoownership.dto.GroupMemberResponse;
import swp302.topic6.evcoownership.dto.UserGroupResponse;
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

    // ==========================
    // 🆕 USER GROUP MANAGEMENT
    // ==========================

    /**
     * Lấy danh sách nhóm của user
     */
    public java.util.List<UserGroupResponse> getUserGroups(Long userId) {
        java.util.List<GroupMember> userMemberships = groupMemberRepository.findByUser_UserIdAndJoinStatus(userId, "active");
        
        return userMemberships.stream().map(member -> {
            CoOwnershipGroup group = member.getGroup();
            Vehicle vehicle = group.getVehicle();
            
            // Tính toán monthly fee (giả sử = estimatedValue / 60 tháng)
            Double monthlyFee = group.getEstimatedValue() != null ? group.getEstimatedValue() / 60 : 0.0;
            
            // Kiểm tra role (creator = admin, others = member)
            String role = group.getCreatedBy().getUserId().equals(userId) ? "admin" : "member";
            
            // Tính tổng ownership percentage của nhóm
            java.util.List<GroupMember> allMembers = groupMemberRepository.findByGroup_GroupIdAndJoinStatus(group.getGroupId(), "active");
            Double totalOwnership = allMembers.stream()
                    .mapToDouble(m -> m.getOwnershipPercentage() != null ? m.getOwnershipPercentage() : 0.0)
                    .sum();
            
            return UserGroupResponse.builder()
                    .id(group.getGroupId())
                    .groupName(group.getGroupName())
                    .description(group.getDescription())
                    .vehicleName(vehicle != null ? vehicle.getBrand() + " " + vehicle.getModel() : "Unknown Vehicle")
                    .vehicleModel(vehicle != null ? vehicle.getModel() : null)
                    .currentMembers(allMembers.size())
                    .maxMembers(group.getMaxMembers())
                    .myOwnershipPercentage(member.getOwnershipPercentage())
                    .totalOwnershipPercentage(totalOwnership)
                    .estimatedValue(group.getEstimatedValue())
                    .monthlyFee(monthlyFee)
                    .status(group.getStatus())
                    .role(role)
                    .createdAt(group.getCreatedAt())
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Chỉnh sửa thông tin nhóm (chỉ admin nhóm)
     */
    public String editGroup(Long groupId, EditGroupRequest request, Long userId) {
        Optional<CoOwnershipGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new RuntimeException("Nhóm không tồn tại!");
        }
        
        CoOwnershipGroup group = groupOpt.get();
        
        // Kiểm tra quyền admin
        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa nhóm này!");
        }
        
        // Cập nhật thông tin
        if (request.getGroupName() != null && !request.getGroupName().trim().isEmpty()) {
            group.setGroupName(request.getGroupName());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        if (request.getMaxMembers() != null && request.getMaxMembers() > 0) {
            // Kiểm tra không được nhỏ hơn số thành viên hiện tại
            int currentMembers = countActiveMembers(groupId);
            if (request.getMaxMembers() < currentMembers) {
                throw new RuntimeException("Số thành viên tối đa không được nhỏ hơn số thành viên hiện tại (" + currentMembers + ")!");
            }
            group.setMaxMembers(request.getMaxMembers());
        }
        if (request.getMinOwnershipPercentage() != null) {
            group.setMinOwnershipPercentage(request.getMinOwnershipPercentage());
        }
        
        groupRepository.save(group);
        return "Cập nhật thông tin nhóm thành công!";
    }

    /**
     * Xóa nhóm (chỉ admin nhóm)
     */
    public String deleteGroup(Long groupId, Long userId) {
        Optional<CoOwnershipGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new RuntimeException("Nhóm không tồn tại!");
        }
        
        CoOwnershipGroup group = groupOpt.get();
        
        // Kiểm tra quyền admin
        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa nhóm này!");
        }
        
        // Xóa tất cả thành viên trước
        java.util.List<GroupMember> members = groupMemberRepository.findByGroup_GroupId(groupId);
        groupMemberRepository.deleteAll(members);
        
        // Cập nhật trạng thái xe về available
        Vehicle vehicle = group.getVehicle();
        if (vehicle != null) {
            vehicle.setStatus("available");
            vehicleRepository.save(vehicle);
        }
        
        // Xóa nhóm
        groupRepository.delete(group);
        
        return "Xóa nhóm thành công!";
    }

    /**
     * Chấp nhận yêu cầu tham gia (admin nhóm)
     */
    public String acceptJoinRequest(Long requestId, Long userId) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findById(requestId);
        if (memberOpt.isEmpty()) {
            throw new RuntimeException("Yêu cầu không tồn tại!");
        }
        
        GroupMember member = memberOpt.get();
        CoOwnershipGroup group = member.getGroup();
        
        // Kiểm tra quyền admin
        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền duyệt yêu cầu cho nhóm này!");
        }
        
        if (!"pending".equals(member.getJoinStatus())) {
            throw new RuntimeException("Yêu cầu đã được xử lý trước đó!");
        }
        
        // Kiểm tra giới hạn thành viên
        int currentMembers = countActiveMembers(group.getGroupId());
        if (currentMembers >= group.getMaxMembers()) {
            throw new RuntimeException("Nhóm đã đạt giới hạn thành viên!");
        }
        
        // Chấp nhận yêu cầu
        member.setJoinStatus("active");
        groupMemberRepository.save(member);
        
        return "Đã chấp nhận yêu cầu tham gia của " + member.getUser().getFullName();
    }

    /**
     * Xóa thành viên khỏi nhóm (admin nhóm)
     */
    public String removeMember(Long groupId, Long memberId, Long userId) {
        Optional<CoOwnershipGroup> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new RuntimeException("Nhóm không tồn tại!");
        }
        
        CoOwnershipGroup group = groupOpt.get();
        
        // Kiểm tra quyền admin
        if (!group.getCreatedBy().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa thành viên khỏi nhóm này!");
        }
        
        // Tìm thành viên cần xóa
        Optional<GroupMember> memberOpt = groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, memberId);
        if (memberOpt.isEmpty()) {
            throw new RuntimeException("Thành viên không tồn tại trong nhóm!");
        }
        
        GroupMember member = memberOpt.get();
        
        // Không cho phép xóa chính mình (admin)
        if (member.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Không thể xóa chính mình khỏi nhóm!");
        }
        
        // Xóa thành viên
        groupMemberRepository.delete(member);
        
        return "Đã xóa " + member.getUser().getFullName() + " khỏi nhóm!";
    }

    /**
     * Lấy danh sách thành viên trong nhóm
     */
    public java.util.List<GroupMemberResponse> getGroupMembers(Long groupId, Long userId) {
        // Kiểm tra user có quyền xem thành viên không (phải là thành viên của nhóm)
        Optional<GroupMember> userMemberOpt = groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, userId);
        if (userMemberOpt.isEmpty()) {
            throw new RuntimeException("Bạn không có quyền xem thành viên của nhóm này!");
        }
        
        java.util.List<GroupMember> members = groupMemberRepository.findByGroup_GroupIdAndJoinStatus(groupId, "active");
        
        return members.stream().map(member -> {
            User user = member.getUser();
            CoOwnershipGroup group = member.getGroup();
            
            // Xác định role
            String role = group.getCreatedBy().getUserId().equals(user.getUserId()) ? "admin" : "member";
            
            return GroupMemberResponse.builder()
                    .id(member.getMemberId())
                    .userId(user.getUserId())
                    .userName(user.getFullName())
                    .userEmail(user.getEmail())
                    .ownershipPercentage(member.getOwnershipPercentage())
                    .role(role)
                    .joinedAt(member.getJoinDate())
                    .status("active")
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Lấy thông tin chi tiết một thành viên
     */
    public Optional<GroupMemberResponse> getGroupMemberById(Long groupId, Long memberId, Long userId) {
        // Kiểm tra user có quyền xem không
        Optional<GroupMember> userMemberOpt = groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, userId);
        if (userMemberOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Optional<GroupMember> memberOpt = groupMemberRepository.findByGroup_GroupIdAndUser_UserId(groupId, memberId);
        if (memberOpt.isEmpty()) {
            return Optional.empty();
        }
        
        GroupMember member = memberOpt.get();
        User user = member.getUser();
        CoOwnershipGroup group = member.getGroup();
        
        String role = group.getCreatedBy().getUserId().equals(user.getUserId()) ? "admin" : "member";
        
        GroupMemberResponse response = 
            GroupMemberResponse.builder()
                .id(member.getMemberId())
                .userId(user.getUserId())
                .userName(user.getFullName())
                .userEmail(user.getEmail())
                .ownershipPercentage(member.getOwnershipPercentage())
                .role(role)
                .joinedAt(member.getJoinDate())
                .status(member.getJoinStatus())
                .build();
                
        return Optional.of(response);
    }
}