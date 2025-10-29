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

    private Vehicle convertToEntity(VehicleRequest request) {
        Vehicle vehicle = Vehicle.builder()
                .model(request.getModel())
                .brand(request.getBrand())
                .licensePlate(request.getLicensePlate())
                .location(request.getLocation())
                .status(request.getStatus())
                .build();

        // Set owner if ownerId is provided
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
        existing.setStatus(request.getStatus());
        existing.setLocation(request.getLocation());

        // Update owner if ownerId is provided
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
                .ownerId(vehicle.getOwner() != null ? vehicle.getOwner().getId() : null)
                .ownerName(vehicle.getOwner() != null ? vehicle.getOwner().getFullName() : null)
                .build();
    }
}