package fu.swp.evcs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fu.swp.evcs.dto.VehicleRequest;
import fu.swp.evcs.dto.VehicleResponse;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.entity.Vehicle;
import fu.swp.evcs.exception.ResourceNotFoundException;
import fu.swp.evcs.repository.UserRepository;
import fu.swp.evcs.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;

/**
 * VehicleService - Handles all vehicle-related logic
 */
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public List<VehicleResponse> getAll() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<VehicleResponse> getByOwnerId(Long ownerId) {
        return vehicleRepository.findAll().stream()
                .filter(v -> v.getOwner() != null && v.getOwner().getId().equals(ownerId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get by ID - Throws exception if not found
     */
    public VehicleResponse getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Xe không tồn tại với ID: " + id));
        return convertToResponse(vehicle);
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        Vehicle vehicle = convertToEntity(request);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return convertToResponse(savedVehicle);
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Xe không tồn tại với ID: " + id));
        
        updateEntityFromRequest(existing, request);
        Vehicle savedVehicle = vehicleRepository.save(existing);
        return convertToResponse(savedVehicle);
    }

    @Transactional
    public VehicleResponse patch(Long id, VehicleRequest request) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Xe không tồn tại với ID: " + id));

        updateEntityFromRequestPartial(existing, request);
        Vehicle savedVehicle = vehicleRepository.save(existing);
        return convertToResponse(savedVehicle);
    }

    @Transactional
    public void delete(Long id) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Xe không tồn tại với ID: " + id));
        vehicleRepository.delete(existing);
    }

    private Vehicle convertToEntity(VehicleRequest request) {
        Vehicle vehicle = Vehicle.builder()
                .model(request.getModel())
                .brand(request.getBrand())
                .licensePlate(request.getLicensePlate())
                .location(request.getLocation())
                .status("pending_approval")
                .registrationInfo(request.getRegistrationInfo())
                .batteryCapacity(request.getBatteryCapacity())
                .yearOfManufacture(request.getYearOfManufacture())
                .imageUrl1(request.getImageUrl1())
                .imageUrl2(request.getImageUrl2())
                .imageUrl3(request.getImageUrl3())
                .verificationStatus("pending")
                .build();

        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            vehicle.setOwner(owner);
        }

        return vehicle;
    }

    private void updateEntityFromRequest(Vehicle existing, VehicleRequest request) {
        existing.setBrand(request.getBrand());
        existing.setModel(request.getModel());
        existing.setLicensePlate(request.getLicensePlate());
        existing.setLocation(request.getLocation());
        existing.setRegistrationInfo(request.getRegistrationInfo());
        existing.setBatteryCapacity(request.getBatteryCapacity());
        existing.setYearOfManufacture(request.getYearOfManufacture());
        existing.setImageUrl1(request.getImageUrl1());
        existing.setImageUrl2(request.getImageUrl2());
        existing.setImageUrl3(request.getImageUrl3());

        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            existing.setOwner(owner);
        }
    }

    private void updateEntityFromRequestPartial(Vehicle existing, VehicleRequest request) {
        if (request.getBrand() != null) existing.setBrand(request.getBrand());
        if (request.getModel() != null) existing.setModel(request.getModel());
        if (request.getLicensePlate() != null) existing.setLicensePlate(request.getLicensePlate());
        if (request.getLocation() != null) existing.setLocation(request.getLocation());
        if (request.getRegistrationInfo() != null) existing.setRegistrationInfo(request.getRegistrationInfo());
        if (request.getBatteryCapacity() != null) existing.setBatteryCapacity(request.getBatteryCapacity());
        if (request.getYearOfManufacture() != null) existing.setYearOfManufacture(request.getYearOfManufacture());
        if (request.getImageUrl1() != null) existing.setImageUrl1(request.getImageUrl1());
        if (request.getImageUrl2() != null) existing.setImageUrl2(request.getImageUrl2());
        if (request.getImageUrl3() != null) existing.setImageUrl3(request.getImageUrl3());
        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            existing.setOwner(owner);
        }
    }

    private VehicleResponse convertToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .vehicleId(vehicle.getId())
                .model(vehicle.getModel())
                .brand(vehicle.getBrand())
                .licensePlate(vehicle.getLicensePlate())
                .location(vehicle.getLocation())
                .status(vehicle.getStatus())
                .registrationInfo(vehicle.getRegistrationInfo())
                .batteryCapacity(vehicle.getBatteryCapacity())
                .yearOfManufacture(vehicle.getYearOfManufacture())
                .imageUrl1(vehicle.getImageUrl1())
                .imageUrl2(vehicle.getImageUrl2())
                .imageUrl3(vehicle.getImageUrl3())
                .verificationStatus(vehicle.getVerificationStatus())
                .rejectReason(vehicle.getRejectReason())
                .verifiedAt(vehicle.getVerifiedAt())
                .ownerId(vehicle.getOwner() != null ? vehicle.getOwner().getId() : null)
                .ownerName(vehicle.getOwner() != null ? vehicle.getOwner().getFullName() : null)
                .verifiedByName(vehicle.getVerifiedBy() != null ? vehicle.getVerifiedBy().getFullName() : null)
                .build();
    }
}