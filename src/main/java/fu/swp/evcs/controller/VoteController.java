package fu.swp.evcs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.entity.Vote;
import fu.swp.evcs.entity.VoteResponse;
import fu.swp.evcs.service.VoteService;
import lombok.RequiredArgsConstructor;

/**
 * VoteController - Clean controller, only calls service
 */
@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<ApiResponse<Vote>> createVote(
            @RequestBody Vote vote,
            @AuthenticationPrincipal User currentUser) {
        Vote created = voteService.createVote(vote, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Tạo phiếu biểu quyết thành công", created));
    }

    @PostMapping("/{voteId}/submit")
    public ResponseEntity<ApiResponse<String>> submitVote(
            @PathVariable Long voteId,
            @RequestParam String response,
            @AuthenticationPrincipal User currentUser) {
        String message = voteService.submitVote(voteId, response, currentUser);
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    @GetMapping("/{voteId}/results")
    public ResponseEntity<ApiResponse<List<VoteResponse>>> getResults(@PathVariable Long voteId) {
        List<VoteResponse> results = voteService.getVoteResults(voteId);
        return ResponseEntity.ok(ApiResponse.success("Lấy kết quả biểu quyết thành công", results));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Vote>>> getVotes(
            @RequestParam(required = false) Long groupId) {
        List<Vote> votes;
        if (groupId != null) {
            votes = voteService.getVotesByGroup(groupId);
        } else {
            votes = voteService.getAllVotes();
        }
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phiếu biểu quyết thành công", votes));
    }
}
