package fu.swp.evcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fu.swp.evcs.entity.Group;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Group, Long> {

    // Lấy các nhóm đang chờ duyệt
    List<Group> findByApprovalStatus(String approvalStatus);

    // Lấy các nhóm theo trạng thái: recruiting, active, closed
    List<Group> findByStatus(String status);
}

