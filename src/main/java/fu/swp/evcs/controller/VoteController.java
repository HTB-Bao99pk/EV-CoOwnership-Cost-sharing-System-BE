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

import fu.swp.evcs.entity.User;
import fu.swp.evcs.entity.Vote;
import fu.swp.evcs.entity.VoteResponse;
import fu.swp.evcs.service.VoteService;
import lombok.RequiredArgsConstructor;

/**
 * ✅ VoteController - Clean controller, chỉ gọi service
 */
@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/create")
    public ResponseEntity<Vote> createVote(
            @RequestBody Vote vote,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(voteService.createVote(vote, currentUser));
    }

    @PostMapping("/{voteId}/submit")
    public ResponseEntity<String> submitVote(
            @PathVariable Long voteId,
            @RequestParam String response,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(voteService.submitVote(voteId, response, currentUser));
    }

    @GetMapping("/{voteId}/results")
    public ResponseEntity<List<VoteResponse>> getResults(@PathVariable Long voteId) {
        return ResponseEntity.ok(voteService.getVoteResults(voteId));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Vote>> getVotesByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(voteService.getVotesByGroup(groupId));
    }
}
