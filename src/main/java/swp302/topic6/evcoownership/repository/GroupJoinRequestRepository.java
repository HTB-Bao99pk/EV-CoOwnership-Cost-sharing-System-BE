// File: GroupJoinRequestRepository.java (SỬA LỖI)
package swp302.topic6.evcoownership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
// Thay thế Entity bị lỗi bằng Entity GroupMember đã tồn tại
import swp302.topic6.evcoownership.entity.GroupMember;
import java.util.List;

public interface GroupJoinRequestRepository extends JpaRepository<GroupMember, Long> {

    // Sử dụng tên phương thức đúng với trường joinStatus trong GroupMember
    List<GroupMember> findByJoinStatus(String status);
}