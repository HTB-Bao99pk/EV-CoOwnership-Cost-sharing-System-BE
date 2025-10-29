package fu.swp.evcs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fu.swp.evcs.entity.Member;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.MemberService;
import lombok.RequiredArgsConstructor;

/**
 * ✅ MemberController - Clean controller cho Member CRUD
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 🟢 GET: Danh sách tất cả members
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    /**
     * 🟢 GET: Chi tiết member theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    /**
     * 🟢 GET: Danh sách members theo group
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Member>> getMembersByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(memberService.getMembersByGroup(groupId));
    }

    /**
     * 🟢 POST: Thêm member vào group
     */
    @PostMapping
    public ResponseEntity<Member> addMember(
            @RequestBody Member member,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(memberService.addMember(member, currentUser));
    }

    /**
     * 🟢 PUT: Cập nhật member
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(
            @PathVariable Long id,
            @RequestBody Member memberUpdate,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(memberService.updateMember(id, memberUpdate, currentUser));
    }

    /**
     * 🟢 DELETE: Xóa member
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        memberService.deleteMember(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
