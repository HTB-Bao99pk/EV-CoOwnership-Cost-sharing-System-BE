package swp302.topic6.evcoownership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp302.topic6.evcoownership.entity.Vehicle;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Lấy danh sách xe theo người sở hữu
    List<Vehicle> findByOwner_UserId(Long userId);

    // Lấy xe theo biển số
    Vehicle findByLicensePlate(String licensePlate);

    // Lấy xe theo khu vực
    List<Vehicle> findByLocation(String location);

    // Lấy danh sách xe đang hoạt động
    List<Vehicle> findByStatus(String status);
}
