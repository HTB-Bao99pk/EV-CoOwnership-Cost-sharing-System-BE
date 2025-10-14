package swp302.topic6.evcoownership.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import swp302.topic6.evcoownership.entity.CoOwnershipGroup;


public interface AdminRepository extends JpaRepository<CoOwnershipGroup, Long> {

    // Lấy các nhóm đang chờ duyệt
    List<CoOwnershipGroup> findByApprovalStatus(String approvalStatus);

    // Lấy các nhóm theo trạng thái: recruiting, active, closed
    List<CoOwnershipGroup> findByStatus(String status);
}

