package swp302.topic6.evcoownership.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swp302.topic6.evcoownership.entity.CoOwnershipGroup;
import swp302.topic6.evcoownership.entity.GroupMember;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // PHƯƠNG THỨC NÀY PHẢI TỒN TẠI
    List<GroupMember> findByJoinStatus(String status);

    // Count active members for a given group
    int countByGroup_GroupIdAndJoinStatus(Long groupId, String joinStatus);

    // Find members for a specific group and join status
    List<GroupMember> findByGroup_GroupIdAndJoinStatus(Long groupId, String joinStatus);

    // 🆕 Additional methods for new APIs
    
    // Find user memberships by user ID and join status
    List<GroupMember> findByUser_UserIdAndJoinStatus(Long userId, String joinStatus);
    
    // Find all members in a group (regardless of status)
    List<GroupMember> findByGroup_GroupId(Long groupId);
    
    // Find specific member in a group by user ID
    Optional<GroupMember> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);
    
    // Count members by group and join status (for AdminService)
    int countByGroupAndJoinStatus(CoOwnershipGroup group, String joinStatus);
}