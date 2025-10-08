package swp302.topic6.evcoownership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp302.topic6.evcoownership.entity.CoOwnershipGroup;

@Repository
public interface CoOwnershipGroupRepository extends JpaRepository<CoOwnershipGroup, Long> {
}
