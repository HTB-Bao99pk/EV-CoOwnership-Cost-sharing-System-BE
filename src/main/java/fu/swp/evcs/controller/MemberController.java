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

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.entity.Member;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.MemberService;
import lombok.RequiredArgsConstructor;

/**
 * MemberController - Clean controller, only calls service
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Member>>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thành viên thành công", members));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> getMemberById(@PathVariable Long id) {
        Member member = memberService.getMemberById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin thành viên thành công", member));
    }



    @PostMapping
    public ResponseEntity<ApiResponse<Member>> addMember(
            @RequestBody Member member,
            @AuthenticationPrincipal User currentUser) {
        Member created = memberService.addMember(member, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Thêm thành viên thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> updateMember(
            @PathVariable Long id,
            @RequestBody Member memberUpdate,
            @AuthenticationPrincipal User currentUser) {
        Member updated = memberService.updateMember(id, memberUpdate, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành viên thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        memberService.deleteMember(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Xóa thành viên thành công", null));
    }
}
