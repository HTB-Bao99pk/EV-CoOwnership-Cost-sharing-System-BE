package fu.swp.evcs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fu.swp.evcs.dto.CreateGroupRequest;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.GroupJoinService;
import fu.swp.evcs.service.GroupService;
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

    @PostMapping("/create")
    public ResponseEntity<String> createGroup(
            @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(groupService.createGroup(request, currentUser));
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<String> requestJoinGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(groupJoinService.requestJoinGroup(groupId, currentUser));
    }
}
