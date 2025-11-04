package fu.swp.evcs.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fu.swp.evcs.dto.ApiResponse;
import fu.swp.evcs.dto.VehicleRequest;
import fu.swp.evcs.dto.VehicleResponse;
import fu.swp.evcs.service.FileUploadService;
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
    private final FileUploadService fileUploadService;

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

    /**
     * Upload vehicle images - Upload ảnh xe (tối đa 3 ảnh)
     * Frontend gọi API này trước để upload ảnh, sau đó dùng URLs trả về để tạo vehicle
     *
     * @param image1 Ảnh thứ 1 (required)
     * @param image2 Ảnh thứ 2 (optional)
     * @param image3 Ảnh thứ 3 (optional)
     * @return Map containing uploaded image URLs
     */
    @PostMapping(value = "/upload-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImages(
            @RequestPart("image1") MultipartFile image1,
            @RequestPart(value = "image2", required = false) MultipartFile image2,
            @RequestPart(value = "image3", required = false) MultipartFile image3) {

        Map<String, String> uploadedUrls = new HashMap<>();

        // Upload image 1 (required)
        String imageUrl1 = fileUploadService.uploadFile(image1, "vehicles");
        uploadedUrls.put("imageUrl1", imageUrl1);

        // Upload image 2 (optional)
        if (image2 != null && !image2.isEmpty()) {
            String imageUrl2 = fileUploadService.uploadFile(image2, "vehicles");
            uploadedUrls.put("imageUrl2", imageUrl2);
        }

        // Upload image 3 (optional)
        if (image3 != null && !image3.isEmpty()) {
            String imageUrl3 = fileUploadService.uploadFile(image3, "vehicles");
            uploadedUrls.put("imageUrl3", imageUrl3);
        }

        return ResponseEntity.ok(ApiResponse.success("Upload ảnh thành công", uploadedUrls));
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