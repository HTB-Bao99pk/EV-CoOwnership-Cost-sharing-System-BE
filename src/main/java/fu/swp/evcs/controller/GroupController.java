package fu.swp.evcs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.CreateGroupRequest;
import fu.swp.evcs.dto.GroupApprovalRequest;
import fu.swp.evcs.dto.GroupResponse;
import fu.swp.evcs.dto.GroupUpdateRequest;
import fu.swp.evcs.dto.JoinGroupRequest;
import fu.swp.evcs.dto.ReviewMemberRequest;
import fu.swp.evcs.entity.Member;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.GroupJoinService;
import fu.swp.evcs.service.GroupService;
import fu.swp.evcs.service.MemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final GroupJoinService groupJoinService;
    private final MemberService memberService;

    // Các method GET/POST/PUT/PATCH/DELETE CRUD (1-6) giữ nguyên...
    // ...

    // 1. GET /api/groups (Lấy danh sách / Lọc)
    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getAllGroups(
            @RequestParam(required = false) String approvalStatus,
            @RequestParam(required = false) Boolean isMember,
            @AuthenticationPrincipal User currentUser) {

        List<GroupResponse> groups;
        if (approvalStatus != null) {
            groups = groupService.getGroupsByApprovalStatus(approvalStatus, currentUser);
        } else if (Boolean.TRUE.equals(isMember)) {
            groups = groupService.getMyGroups(currentUser);
        } else {
            groups = groupService.getAllGroups();
        }

        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách nhóm thành công", groups));
    }

    // 2. GET /api/groups/{groupId}
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(@PathVariable Long groupId) {
        GroupResponse group = groupService.getGroupById(groupId);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin nhóm thành công", group));
    }

    // 3. POST /api/groups
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createGroup(
            @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal User currentUser) {
        String message = groupService.createGroup(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    // 4. PUT /api/groups/{groupId}
    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable Long groupId,
            @RequestBody GroupUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {
        GroupResponse updatedGroup = groupService.updateGroup(groupId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật toàn bộ nhóm thành công", updatedGroup));
    }

    // 5. PATCH /api/groups/{groupId}
    @PatchMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> partialUpdateGroup(
            @PathVariable Long groupId,
            @RequestBody GroupUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {
        GroupResponse updatedGroup = groupService.partialUpdateGroup(groupId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật một phần nhóm thành công", updatedGroup));
    }

    // 6. DELETE /api/groups/{groupId}
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser) {
        groupService.deleteGroup(groupId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Xóa nhóm thành công", null));
    }

    // API DUYỆT ADMIN: PATCH /api/groups/{id}/approval
    @PatchMapping("/{groupId}/approval")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> handleGroupApproval(
            @PathVariable Long groupId,
            @RequestBody GroupApprovalRequest request,
            @AuthenticationPrincipal User currentAdmin) {

        // SỬA ĐỔI: BỎ THAM SỐ reason. Chỉ truyền groupId, approved, currentAdmin.
        String message = groupService.handleGroupApproval(
                groupId,
                request.isApproved(),
                currentAdmin
        );
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    // API RỜI NHÓM: DELETE /api/groups/{id}/members/me
    @DeleteMapping("/{groupId}/members/me")
    public ResponseEntity<ApiResponse<Void>> leaveGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser) {

        groupService.leaveGroup(groupId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Rời nhóm thành công", null));
    }

    // Các API khác giữ nguyên
    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse<String>> requestJoinGroup(
            @PathVariable Long groupId,
            @RequestBody JoinGroupRequest request,
            @AuthenticationPrincipal User currentUser) {
        String message = groupJoinService.requestJoinGroup(groupId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    @PostMapping("/{groupId}/members/{memberId}/review")
    public ResponseEntity<ApiResponse<String>> reviewMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @RequestBody ReviewMemberRequest request,
            @AuthenticationPrincipal User currentUser) {
        String message = groupJoinService.reviewMemberRequest(groupId, memberId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<List<Member>>> getGroupMembers(@PathVariable Long groupId) {
        List<Member> members = memberService.getMembersByGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thành viên nhóm thành công", members));
    }

    @GetMapping("/{groupId}/available-ownership")
    public ResponseEntity<ApiResponse<Double>> getAvailableOwnership(@PathVariable Long groupId) {
        Double available = groupService.getAvailableOwnership(groupId);
        return ResponseEntity.ok(ApiResponse.success("Lấy % sở hữu còn lại thành công", available));
    }

    @GetMapping("/{groupId}/my-ownership")
    public ResponseEntity<ApiResponse<Member>> getMyOwnership(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser) {
        Member ownership = groupService.getMyOwnership(groupId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin sở hữu thành công", ownership));
    }
}