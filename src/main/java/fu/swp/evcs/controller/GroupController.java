package fu.swp.evcs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.CreateGroupRequest;
import fu.swp.evcs.dto.GroupResponse;
import fu.swp.evcs.dto.JoinGroupRequest;
import fu.swp.evcs.dto.ReviewMemberRequest;
import fu.swp.evcs.entity.Member;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.GroupJoinService;
import fu.swp.evcs.service.GroupService;
import fu.swp.evcs.service.MemberService;
import lombok.RequiredArgsConstructor;

/**
 * GroupController - Clean controller, only calls service
 *
 * Controller does NOT contain logic, only:
 * 1. Receives request + @AuthenticationPrincipal User
 * 2. Calls service (service handles validation + logic)
 * 3. Returns response (single line only)
 */
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final GroupJoinService groupJoinService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createGroup(
            @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal User currentUser) {
        String message = groupService.createGroup(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getAllGroups(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String approvalStatus) {
        List<GroupResponse> groups = groupService.getGroupsFiltered(status, approvalStatus);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách nhóm thành công", groups));
    }

    @GetMapping("/my-groups")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getMyGroups(@AuthenticationPrincipal User currentUser) {
        List<GroupResponse> groups = groupService.getMyGroups(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách nhóm của bạn thành công", groups));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(@PathVariable Long groupId) {
        GroupResponse group = groupService.getGroupById(groupId);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin nhóm thành công", group));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable Long groupId,
            @RequestBody java.util.Map<String, Object> requestBody) {
        GroupResponse updated = groupService.updateGroup(groupId, requestBody);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật nhóm thành công", updated));
    }

    @PatchMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> patchGroup(
            @PathVariable Long groupId,
            @RequestBody java.util.Map<String, Object> requestBody) {
        GroupResponse updated = groupService.patchGroup(groupId, requestBody);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật nhóm (một phần) thành công", updated));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success("Xóa nhóm thành công", null));
    }

    // ========== Member Management (Nested Resource) ==========
    
    @GetMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<List<Member>>> getGroupMembers(@PathVariable Long groupId) {
        List<Member> members = memberService.getMembersByGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thành viên nhóm thành công", members));
    }

    // ========== Additional Group APIs ==========
    
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

    @PostMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse<String>> leaveGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser) {
        String message = groupService.leaveGroup(groupId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }
}

