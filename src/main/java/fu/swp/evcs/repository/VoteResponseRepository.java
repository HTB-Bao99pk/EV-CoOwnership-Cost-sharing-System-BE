package fu.swp.evcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fu.swp.evcs.entity.VoteResponse;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface VoteResponseRepository extends JpaRepository<VoteResponse, Long> {
    Optional<VoteResponse> findByVoteIdAndUserId(Long voteId, Long userId);
    List<VoteResponse> findByVoteId(Long voteId);
}
