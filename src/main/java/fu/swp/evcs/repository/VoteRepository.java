package fu.swp.evcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fu.swp.evcs.entity.Vote;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByGroupId(Long groupId);
}
