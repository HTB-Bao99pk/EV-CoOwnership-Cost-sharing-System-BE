package swp302.topic6.evcoownership.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swp302.topic6.evcoownership.entity.Vehicle;

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

    // Tìm theo mục đích sử dụng (exact match)
    List<Vehicle> findByPurpose(String purpose);

    // Tìm theo mục đích chứa chuỗi (case-insensitive)
    List<Vehicle> findByPurposeContainingIgnoreCase(String partialPurpose);

    // Tìm theo mục đích và trạng thái (ví dụ: purpose='personal' and status='available')
    List<Vehicle> findByPurposeAndStatus(String purpose, String status);

    // Pageable variants for listing with pagination
    Page<Vehicle> findByPurposeContainingIgnoreCase(String partialPurpose, Pageable pageable);
    Page<Vehicle> findByStatus(String status, Pageable pageable);
}
