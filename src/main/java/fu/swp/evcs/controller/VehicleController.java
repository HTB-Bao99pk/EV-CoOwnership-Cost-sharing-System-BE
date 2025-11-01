package fu.swp.evcs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.VehicleRequest;
import fu.swp.evcs.dto.VehicleResponse;
import fu.swp.evcs.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * VehicleController - Clean controller, only calls service
 */
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAll(
            @RequestParam(required = false) Long ownerId) {
        List<VehicleResponse> vehicles;
        if (ownerId != null) {
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
        return ResponseEntity.ok(ApiResponse.success("Tạo xe thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> update(
            @PathVariable Long id, 
            @RequestBody @Valid VehicleRequest vehicleRequest) {
        VehicleResponse updated = vehicleService.update(id, vehicleRequest);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật xe thành công", updated));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> patch(
            @PathVariable Long id,
            @RequestBody VehicleRequest vehicleRequest) {
        VehicleResponse updated = vehicleService.patch(id, vehicleRequest);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật một phần xe thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa xe thành công", null));
    }
}