package swp302.topic6.evcoownership.repository;

import swp302.topic6.evcoownership.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCccd(String cccd);
}
