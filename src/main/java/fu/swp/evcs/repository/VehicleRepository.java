package fu.swp.evcs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fu.swp.evcs.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByOwner_Id(Long userId);

    Vehicle findByLicensePlate(String licensePlate);

    List<Vehicle> findByLocation(String location);

    List<Vehicle> findByStatus(String status);

    List<Vehicle> findByVerificationStatus(String verificationStatus);
}
