package fu.swp.evcs.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.ApprovalRequest; // DTO mới
import fu.swp.evcs.dto.VehicleRequest;
import fu.swp.evcs.dto.VehicleResponse;
import fu.swp.evcs.entity.User;
import fu.swp.evcs.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // SỬA ĐỔI: Thêm Query Param để lọc (thay thế GET /api/admin/pending-vehicles)
    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAll(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String verificationStatus) {

        List<VehicleResponse> vehicles;
        if (verificationStatus != null) {
            vehicles = vehicleService.getVehiclesByVerificationStatus(verificationStatus);
        } else if (ownerId != null) {
            vehicles = vehicleService.getByOwnerId(ownerId);
        } else {
            vehicles = vehicleService.getAll();
        }
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách xe thành công", vehicles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getById(@PathVariable Long id) {
        VehicleResponse vehicle = vehicleService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin xe thành công", vehicle));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> create(@RequestBody @Valid VehicleRequest vehicleRequest) {
        VehicleResponse created = vehicleService.create(vehicleRequest);
        return ResponseEntity.created(URI.create("/api/vehicles/" + created.getVehicleId()))
                .body(ApiResponse.success("Tạo xe thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid VehicleRequest vehicleRequest) {
        VehicleResponse updated = vehicleService.update(id, vehicleRequest);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật xe thành công", updated));
    }

    // NEW: API Duyệt Admin (Thay thế POST /api/admin/approve-vehicle)
    @PatchMapping("/{vehicleId}/approval")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> approveVehicle(
            @PathVariable Long vehicleId,
            @RequestBody ApprovalRequest request,
            @AuthenticationPrincipal User currentAdmin) {

        String message = vehicleService.handleVehicleApproval(
                vehicleId,
                request.isApproved(),
                currentAdmin
        );
        return ResponseEntity.ok(ApiResponse.success(message, message));
    }

    // THIẾU: PATCH /api/vehicles/{id} và DELETE /api/vehicles/{id} (Nếu muốn đủ 5 method)
}