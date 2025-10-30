package fu.swp.evcs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fu.swp.evcs.entity.GroupTransaction;

@Repository
public interface GroupTransactionRepository extends JpaRepository<GroupTransaction, Long> {
    
    List<GroupTransaction> findByGroup_IdOrderByCreatedAtDesc(Long groupId);
    
    List<GroupTransaction> findByUser_IdOrderByCreatedAtDesc(Long userId);
    
    List<GroupTransaction> findByGroup_IdAndTransactionType(Long groupId, String transactionType);
}
