package fu.swp.evcs.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // CẦN THIẾT CHO CÁC METHOD THAY ĐỔI DỮ LIỆU

import fu.swp.evcs.dto.CreateGroupRequest;
import fu.swp.evcs.dto.GroupResponse;
import fu.swp.evcs.dto.GroupUpdateRequest; // Cần import DTO mới
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

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final VehicleRepository vehicleRepository;
    private final MemberRepository memberRepository;

    // =========================================================================
    // PHƯƠNG THỨC HIỆN TẠI (Được giữ nguyên)
    // =========================================================================

    @Transactional
    public String createGroup(CreateGroupRequest request, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        if (!"verified".equalsIgnoreCase(currentUser.getVerificationStatus())) {
            throw new ForbiddenException("Tài khoản chưa được xác minh!");
        }

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Xe không tồn tại!"));

        if (vehicle.getOwner() == null || !vehicle.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không phải chủ sở hữu xe này!");
        }

        boolean vehicleInOtherGroup = groupRepository.findAll().stream()
                .anyMatch(g -> g.getVehicle() != null
                        && g.getVehicle().getId().equals(vehicle.getId())
                        && !"rejected".equalsIgnoreCase(g.getApprovalStatus())
                        && !"closed".equalsIgnoreCase(g.getStatus()));

        if (vehicleInOtherGroup) {
            throw new BadRequestException("Xe này đã nằm trong nhóm khác hoặc nhóm đó đang hoạt động/chờ duyệt!");
        }

        if (!"available".equalsIgnoreCase(vehicle.getStatus())) {
            throw new BadRequestException("Xe này hiện không sẵn sàng để tạo nhóm! Trạng thái: " + vehicle.getStatus());
        }

        if (!"approved".equalsIgnoreCase(vehicle.getVerificationStatus())) {
            throw new BadRequestException("Xe này chưa được admin duyệt!");
        }

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

        vehicle.setStatus("pending_approval");
        vehicleRepository.save(vehicle);

        Member ownerMember = Member.builder()
                .group(group)
                .user(currentUser)
                .ownershipPercentage(100.0)
                .joinStatus("approved")
                .joinDate(new Date())
                .build();

        memberRepository.save(ownerMember);

        return "Tạo nhóm thành công! Nhóm đang chờ admin duyệt.";
    }

    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<GroupResponse> getMyGroups(User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        List<Member> myMemberships = memberRepository.findByUser_Id(currentUser.getId());

        return myMemberships.stream()
                .map(member -> convertToResponse(member.getGroup()))
                .collect(Collectors.toList());
    }

    public GroupResponse getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));
        return convertToResponse(group);
    }

    public List<Group> getRecruitingGroups() {
        return groupRepository.findByStatus("recruiting");
    }

    public Double getAvailableOwnership(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        Double totalOwnership = memberRepository.findByGroup_Id(groupId).stream()
                .mapToDouble(Member::getOwnershipPercentage)
                .sum();

        return 100.0 - totalOwnership;
    }

    public Member getMyOwnership(Long groupId, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        return memberRepository.findByGroup_IdAndUser_Id(groupId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bạn không phải thành viên của nhóm này!"));
    }

    @Transactional
    public String leaveGroup(Long groupId, User currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập!");
        }

        groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        Member member = memberRepository.findByGroup_IdAndUser_Id(groupId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bạn không phải thành viên của nhóm này!"));

        if (member.getGroup().getCreatedBy().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Chủ nhóm không thể rời khỏi nhóm!");
        }

        memberRepository.delete(member);
        return "Rời nhóm thành công!";
    }

    private GroupResponse convertToResponse(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .status(group.getStatus())
                .approvalStatus(group.getApprovalStatus())
                .estimatedValue(group.getEstimatedValue())
                .maxMembers(group.getMaxMembers())
                .minOwnershipPercentage(group.getMinOwnershipPercentage())
                .totalOwnershipPercentage(group.getTotalOwnershipPercentage())
                .isLocked(group.getIsLocked())
                .contractUrl(group.getContractUrl())
                .balance(group.getBalance())
                .createdAt(group.getCreatedAt())
                .vehicleId(group.getVehicle() != null ? group.getVehicle().getId() : null)
                .vehicleBrand(group.getVehicle() != null ? group.getVehicle().getBrand() : null)
                .vehicleModel(group.getVehicle() != null ? group.getVehicle().getModel() : null)
                .vehicleLicensePlate(group.getVehicle() != null ? group.getVehicle().getLicensePlate() : null)
                .createdById(group.getCreatedBy() != null ? group.getCreatedBy().getId() : null)
                .createdByName(group.getCreatedBy() != null ? group.getCreatedBy().getFullName() : null)
                .approvedById(group.getApprovedBy() != null ? group.getApprovedBy().getId() : null)
                .approvedByName(group.getApprovedBy() != null ? group.getApprovedBy().getFullName() : null)
                .rejectReason(group.getRejectReason())
                .build();
    }
    // Hỗ trợ GET /api/groups?approvalStatus=pending (Lọc theo Query Param)
    public List<GroupResponse> getGroupsByApprovalStatus(String approvalStatus, User currentUser) {
        // Kiểm tra quyền Admin
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            throw new ForbiddenException("Bạn không có quyền truy cập trạng thái duyệt nhóm!");
        }

        // Cần phương thức findByApprovalStatus trong GroupRepository
        return groupRepository.findByApprovalStatus(approvalStatus).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Hỗ trợ PUT /api/groups/{id} (Cập nhật toàn bộ)
    @Transactional
    public GroupResponse updateGroup(Long groupId, GroupUpdateRequest request, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        if (!group.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không có quyền cập nhật nhóm này!");
        }

        // Cập nhật TẤT CẢ các trường (Yêu cầu của PUT)
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setEstimatedValue(request.getEstimatedValue());
        group.setMaxMembers(request.getMaxMembers());
        group.setMinOwnershipPercentage(request.getMinOwnershipPercentage());

        return convertToResponse(groupRepository.save(group));
    }

    // Hỗ trợ PATCH /api/groups/{id} (Cập nhật một phần)
    @Transactional
    public GroupResponse partialUpdateGroup(Long groupId, GroupUpdateRequest request, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        if (!group.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không có quyền cập nhật nhóm này!");
        }

        // Cập nhật CÁC TRƯỜNG KHÔNG NULL (Yêu cầu của PATCH)
        if (request.getName() != null) {
            group.setName(request.getName());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        if (request.getEstimatedValue() != null) {
            group.setEstimatedValue(request.getEstimatedValue());
        }
        if (request.getMaxMembers() != null) {
            group.setMaxMembers(request.getMaxMembers());
        }
        // ... Thêm logic PATCH cho các trường khác ...

        return convertToResponse(groupRepository.save(group));
    }

    // Hỗ trợ DELETE /api/groups/{id}
    @Transactional
    public void deleteGroup(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        // Kiểm tra quyền (chỉ Admin hoặc người tạo nhóm)
        if (!"ADMIN".equals(currentUser.getRole()) && !group.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không có quyền xóa nhóm này!");
        }

        // Xóa các thành viên liên quan
        // Bạn cần thêm phương thức deleteByGroup_Id(Long groupId) vào MemberRepository
        memberRepository.deleteByGroup_Id(groupId);

        groupRepository.delete(group);
    }

    // Hỗ trợ PATCH /api/groups/{id}/approval (Logic duyệt Admin ĐÃ BỎ reason)
    @Transactional
    public String handleGroupApproval(Long groupId, boolean approved, User currentAdmin) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        if (!"pending".equalsIgnoreCase(group.getApprovalStatus())) {
            throw new BadRequestException("Nhóm này đã được xử lý duyệt trước đó!");
        }

        group.setApprovedBy(currentAdmin);

        if (approved) {
            group.setApprovalStatus("approved");
            group.setStatus("recruiting");
            group.setRejectReason(null); // Xóa lý do từ chối nếu có
            groupRepository.save(group);
            return "Nhóm đã được duyệt thành công!";
        } else {

            group.setApprovalStatus("rejected");
            group.setRejectReason("Đã từ chối bởi Admin."); // Đặt lý do mặc định
            group.setStatus("closed");

            // Hoàn tác trạng thái xe
            if (group.getVehicle() != null) {
                Vehicle vehicle = group.getVehicle();
                vehicle.setStatus("available");
                vehicleRepository.save(vehicle);
            }

            groupRepository.save(group);
            return "Nhóm đã bị từ chối duyệt!";
        }
    }
}