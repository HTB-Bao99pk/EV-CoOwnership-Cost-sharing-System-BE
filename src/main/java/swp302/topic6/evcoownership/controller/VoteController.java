package swp302.topic6.evcoownership.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp302.topic6.evcoownership.entity.Vote;
import swp302.topic6.evcoownership.dto.VoteResponse;
import swp302.topic6.evcoownership.service.VoteService;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VoteController {

    private final VoteService voteService;

    //  Tạo phiếu mới
    @PostMapping("/create")
    public Vote createVote(@RequestBody Vote vote) {
        return voteService.createVote(vote);
    }

    //  Người dùng bỏ phiếu (YES / NO)
    @PostMapping("/{voteId}/submit")
    public String submitVote(
            @PathVariable Long voteId,
            @RequestParam Long userId,
            @RequestParam String response
    ) {
        return voteService.submitVote(voteId, userId, response);
    }

    //  Xem kết quả của 1 vote
    @GetMapping("/{voteId}/results")
    public List<VoteResponse> getResults(@PathVariable Long voteId) {
        return voteService.getVoteResults(voteId);
    }

    //  Xem tất cả phiếu của 1 nhóm
    @GetMapping("/group/{groupId}")
    public List<Vote> getVotesByGroup(@PathVariable Long groupId) {
        return voteService.getVotesByGroup(groupId);
    }
}