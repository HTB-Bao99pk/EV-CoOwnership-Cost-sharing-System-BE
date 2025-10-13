package swp302.topic6.evcoownership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp302.topic6.evcoownership.entity.CoOwnershipGroup;
import java.util.List;

@Repository
public interface CoOwnershipGroupRepository extends JpaRepository<CoOwnershipGroup, Long> {

    // 🔹 Lấy tất cả nhóm đang chờ admin duyệt
    List<CoOwnershipGroup> findByApprovalStatus(String status);

    // 🔹 Lấy nhóm theo trạng thái (recruiting, active, closed)
    List<CoOwnershipGroup> findByStatus(String status);
}
