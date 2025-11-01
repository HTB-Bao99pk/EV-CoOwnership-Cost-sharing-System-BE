package fu.swp.evcs.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import fu.swp.evcs.dto.CreateGroupRequest;
import fu.swp.evcs.dto.GroupResponse;
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

    public List<GroupResponse> getGroupsFiltered(String status, String approvalStatus) {
        List<Group> groups;
        if (status != null && !status.isBlank()) {
            groups = groupRepository.findByStatus(status);
        } else if (approvalStatus != null && !approvalStatus.isBlank()) {
            groups = groupRepository.findByApprovalStatus(approvalStatus);
        } else {
            groups = groupRepository.findAll();
        }
        return groups.stream().map(this::convertToResponse).collect(Collectors.toList());
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

    public GroupResponse updateGroup(Long groupId, Map<String, Object> requestBody) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        applyGroupUpdates(group, requestBody, true);
        groupRepository.save(group);
        return convertToResponse(group);
    }

    public GroupResponse patchGroup(Long groupId, Map<String, Object> requestBody) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));

        applyGroupUpdates(group, requestBody, false);
        groupRepository.save(group);
        return convertToResponse(group);
    }

    public void deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhóm không tồn tại!"));
        groupRepository.delete(group);
    }

    private void applyGroupUpdates(Group group, Map<String, Object> body, boolean fullUpdate) {
        if (body == null) return;
        if (fullUpdate || body.containsKey("name")) {
            Object v = body.get("name");
            if (v != null) group.setName(String.valueOf(v));
        }
        if (fullUpdate || body.containsKey("description")) {
            Object v = body.get("description");
            if (v != null) group.setDescription(String.valueOf(v));
        }
        if (fullUpdate || body.containsKey("status")) {
            Object v = body.get("status");
            if (v != null) group.setStatus(String.valueOf(v));
        }
        if (fullUpdate || body.containsKey("approvalStatus")) {
            Object v = body.get("approvalStatus");
            if (v != null) group.setApprovalStatus(String.valueOf(v));
        }
        if (fullUpdate || body.containsKey("rejectReason")) {
            Object v = body.get("rejectReason");
            group.setRejectReason(v != null ? String.valueOf(v) : null);
        }
        if (fullUpdate || body.containsKey("estimatedValue")) {
            Object v = body.get("estimatedValue");
            if (v != null) group.setEstimatedValue(Double.valueOf(String.valueOf(v)));
        }
        if (fullUpdate || body.containsKey("maxMembers")) {
            Object v = body.get("maxMembers");
            if (v != null) group.setMaxMembers(Integer.valueOf(String.valueOf(v)));
        }
        if (fullUpdate || body.containsKey("minOwnershipPercentage")) {
            Object v = body.get("minOwnershipPercentage");
            if (v != null) group.setMinOwnershipPercentage(new java.math.BigDecimal(String.valueOf(v)));
        }
        if (fullUpdate || body.containsKey("isLocked")) {
            Object v = body.get("isLocked");
            if (v != null) group.setIsLocked(Boolean.parseBoolean(String.valueOf(v)));
        }
        if (fullUpdate || body.containsKey("balance")) {
            Object v = body.get("balance");
            if (v != null) group.setBalance(new java.math.BigDecimal(String.valueOf(v)));
        }
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
}
