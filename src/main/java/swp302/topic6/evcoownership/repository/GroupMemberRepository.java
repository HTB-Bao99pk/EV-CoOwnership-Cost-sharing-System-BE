package swp302.topic6.evcoownership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp302.topic6.evcoownership.entity.GroupMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByJoinStatus(String status);

    // 🔹 Lấy tất cả thành viên trong một group (dùng groupId trong entity CoOwnershipGroup)
    List<GroupMember> findByGroup_GroupId(Long groupId);

    // 🔹 Tìm 1 thành viên cụ thể trong nhóm
    Optional<GroupMember> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);
}
