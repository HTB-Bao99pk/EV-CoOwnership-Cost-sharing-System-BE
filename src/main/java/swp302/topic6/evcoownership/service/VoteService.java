package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.entity.Vote;
import swp302.topic6.evcoownership.dto.VoteResponse;
import swp302.topic6.evcoownership.repository.VoteRepository;
import swp302.topic6.evcoownership.repository.VoteResponseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteResponseRepository voteResponseRepository;

    //  Tạo phiếu mới
    public Vote createVote(Vote vote) {
        return voteRepository.save(vote);
    }

    //  Thành viên bỏ phiếu (YES / NO)
    public String submitVote(Long voteId, Long userId, String response) {
        // Kiểm tra người này đã vote chưa
        if (voteResponseRepository.findByVoteIdAndUserId(voteId, userId).isPresent()) {
            return "Bạn đã bỏ phiếu cho chủ đề này rồi!";
        }

        VoteResponse voteResponse = VoteResponse.builder()
                .voteId(voteId)
                .userId(userId)
                .response(response.toUpperCase())
                .build();

        voteResponseRepository.save(voteResponse);
        return "Bỏ phiếu thành công!";
    }

    //  Xem kết quả vote
    public List<VoteResponse> getVoteResults(Long voteId) {
        return voteResponseRepository.findByVoteId(voteId);
    }

    // Xem tất cả các cuộc vote trong nhóm
    public List<Vote> getVotesByGroup(Long groupId) {
        return voteRepository.findByGroupId(groupId);
    }
}
