package fu.swp.evcs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fu.swp.evcs.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByGroup_IdOrderByCreatedAtDesc(Long groupId);
    
    List<Payment> findByMember_IdOrderByCreatedAtDesc(Long memberId);
    
    List<Payment> findByStatus(String status);
}
