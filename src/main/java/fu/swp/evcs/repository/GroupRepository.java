package fu.swp.evcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import fu.swp.evcs.entity.Group;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByApprovalStatus(String status);

    List<Group> findByStatus(String status);
}
