package fu.swp.evcs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fu.swp.evcs.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByJoinStatus(String status);

    // 🔹 Lấy tất cả thành viên trong một group (sử dụng field `id` của Group entity)
    List<Member> findByGroup_Id(Long groupId);

    // 🔹 Tìm 1 thành viên cụ thể trong nhóm
    Optional<Member> findByGroup_IdAndUser_Id(Long groupId, Long userId);
}
