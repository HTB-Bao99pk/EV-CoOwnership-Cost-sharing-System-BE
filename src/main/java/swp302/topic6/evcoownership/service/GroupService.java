package swp302.topic6.evcoownership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp302.topic6.evcoownership.dto.CreateGroupRequest;
import swp302.topic6.evcoownership.entity.CoOwnershipGroup;
import swp302.topic6.evcoownership.entity.User;
import swp302.topic6.evcoownership.entity.Vehicle;
import swp302.topic6.evcoownership.repository.CoOwnershipGroupRepository;
import swp302.topic6.evcoownership.repository.UserRepository;
import swp302.topic6.evcoownership.repository.VehicleRepository;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final CoOwnershipGroupRepository groupRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public String createGroup(CreateGroupRequest request, Long userId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(request.getVehicleId());
        Optional<User> userOpt = userRepository.findById(userId);

        if (vehicleOpt.isEmpty() || userOpt.isEmpty()) {
            return "Xe hoặc người dùng không tồn tại!";
        }

        Vehicle vehicle = vehicleOpt.get();
        User creator = userOpt.get();

        // ❌ Nếu không phải chủ xe → không được phép tạo nhóm
        if (vehicle.getOwner() == null || !vehicle.getOwner().getUserId().equals(userId)) {
            return "Bạn không phải chủ sở hữu xe này!";
        }

        // ❌ Nếu xe đang thuộc nhóm khác → chặn
        if (!"available".equalsIgnoreCase(vehicle.getStatus())) {
            return "Xe này hiện đang thuộc nhóm khác hoặc đang chờ duyệt!";
        }

        // ✅ Tạo nhóm chia sẻ xe
        CoOwnershipGroup group = CoOwnershipGroup.builder()
                .vehicle(vehicle)
                .createdBy(creator)
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .estimatedValue(request.getEstimatedValue())
                .status("recruiting")
                .approvalStatus("pending")
                .createdAt(new Date()) // Giả định trường này là 'createdAt'
                .build();

        groupRepository.save(group);

        // ✅ Cập nhật trạng thái xe sang "pending_approval"
        vehicle.setStatus("pending_approval");
        vehicleRepository.save(vehicle);

        return "Tạo nhóm thành công! Đang chờ admin duyệt.";
    }
}