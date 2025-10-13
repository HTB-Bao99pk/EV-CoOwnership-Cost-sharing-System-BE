package swp302.topic6.evcoownership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp302.topic6.evcoownership.entity.GroupMember;
import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // PHƯƠNG THỨC NÀY PHẢI TỒN TẠI
    List<GroupMember> findByJoinStatus(String status);
}