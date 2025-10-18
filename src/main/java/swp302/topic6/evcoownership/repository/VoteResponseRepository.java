package swp302.topic6.evcoownership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swp302.topic6.evcoownership.entity.VoteResponse;

import java.util.Optional;
import java.util.List;

public interface VoteResponseRepository extends JpaRepository<VoteResponse, Long> {
    Optional<VoteResponse> findByVoteIdAndUserId(Long voteId, Long userId);
    List<VoteResponse> findByVoteId(Long voteId);
}
