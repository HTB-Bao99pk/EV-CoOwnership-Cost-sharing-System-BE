package fu.swp.evcs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fu.swp.evcs.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Lấy danh sách xe theo người sở hữu (sử dụng field `id` của User entity)
    List<Vehicle> findByOwner_Id(Long userId);

    // Lấy xe theo biển số
    Vehicle findByLicensePlate(String licensePlate);

    // Lấy xe theo khu vực
    List<Vehicle> findByLocation(String location);

    // Lấy danh sách xe đang hoạt động
    List<Vehicle> findByStatus(String status);
}
