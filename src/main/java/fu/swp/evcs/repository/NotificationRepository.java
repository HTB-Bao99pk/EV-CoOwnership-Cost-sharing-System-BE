package fu.swp.evcs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fu.swp.evcs.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);
    
    List<Notification> findByUser_IdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead);
    
    Long countByUser_IdAndIsRead(Long userId, Boolean isRead);
}
