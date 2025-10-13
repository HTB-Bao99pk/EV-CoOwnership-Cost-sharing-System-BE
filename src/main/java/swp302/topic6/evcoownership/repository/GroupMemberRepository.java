package swp302.topic6.evcoownership.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swp302.topic6.evcoownership.entity.GroupMember;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // PHƯƠNG THỨC NÀY PHẢI TỒN TẠI
    List<GroupMember> findByJoinStatus(String status);

    // Count active members for a given group
    int countByGroup_GroupIdAndJoinStatus(Long groupId, String joinStatus);

    // Find members for a specific group and join status
    List<GroupMember> findByGroup_GroupIdAndJoinStatus(Long groupId, String joinStatus);
}