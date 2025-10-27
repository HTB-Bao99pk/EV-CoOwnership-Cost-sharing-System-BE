package fu.swp.evcs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fu.swp.evcs.entity.User;
import fu.swp.evcs.entity.Vote;
import fu.swp.evcs.entity.VoteResponse;
import fu.swp.evcs.exception.BadRequestException;
import fu.swp.evcs.exception.ForbiddenException;
import fu.swp.evcs.exception.UnauthorizedException;
import fu.swp.evcs.repository.VoteRepository;
import fu.swp.evcs.repository.VoteResponseRepository;
import lombok.RequiredArgsConstructor;

/**
 * ✅ VoteService - Xử lý tất cả logic voting
 */
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteResponseRepository voteResponseRepository;

    /**
     * Tạo phiếu mới - XỬ LÝ VALIDATION
     */
    public Vote createVote(Vote vote, User currentUser) {
        // 1. Validation authentication
        if (currentUser == null) {
            throw new UnauthorizedException("Chưa đăng nhập");
        }
        
        // 2. Validation verification
        if (!"verified".equalsIgnoreCase(currentUser.getVerificationStatus())) {
            throw new ForbiddenException("Tài khoản chưa được xác minh");
        }
        
        // 3. Lưu vote
        return voteRepository.save(vote);
    }

    /**
     * Bỏ phiếu - XỬ LÝ VALIDATION
     */
    public String submitVote(Long voteId, String response, User currentUser) {
        // 1. Validation authentication
        if (currentUser == null) {
            throw new UnauthorizedException("Chưa đăng nhập");
        }
        
        // 2. Validation verification
        if (!"verified".equalsIgnoreCase(currentUser.getVerificationStatus())) {
            throw new ForbiddenException("Tài khoản chưa được xác minh");
        }
        
        // 3. Kiểm tra đã vote chưa
        if (voteResponseRepository.findByVoteIdAndUserId(voteId, currentUser.getId()).isPresent()) {
            throw new BadRequestException("Bạn đã bỏ phiếu cho chủ đề này rồi!");
        }

        // 4. Lưu vote response
        VoteResponse voteResponse = VoteResponse.builder()
                .voteId(voteId)
                .userId(currentUser.getId())
                .response(response.toUpperCase())
                .build();

        voteResponseRepository.save(voteResponse);
        return "Bỏ phiếu thành công!";
    }

    /**
     * Xem kết quả vote
     */
    public List<VoteResponse> getVoteResults(Long voteId) {
        return voteResponseRepository.findByVoteId(voteId);
    }

    /**
     * Xem tất cả vote trong nhóm
     */
    public List<Vote> getVotesByGroup(Long groupId) {
        return voteRepository.findByGroupId(groupId);
    }
}
