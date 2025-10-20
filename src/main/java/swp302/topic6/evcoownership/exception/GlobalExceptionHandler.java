package swp302.topic6.evcoownership.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import swp302.topic6.evcoownership.dto.ApiResponse;

/**
 * ⭐️ TỐI ƯU: Xử lý exception tập trung cho toàn bộ ứng dụng.
 * Bắt tất cả RuntimeException và trả về lỗi 400 (Bad Request)
 * hoặc 401 (Unauthorized) với message rõ ràng.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        // Log lỗi ra console (quan trọng khi dev)
        ex.printStackTrace();

        String message = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        // Phân loại lỗi để trả về status code chính xác hơn
        if (message.contains("Vui lòng đăng nhập") || message.contains("không có quyền")) {
            status = HttpStatus.UNAUTHORIZED; // 401
        } else if (message.contains("không tồn tại") || message.contains("Không tìm thấy")) {
            status = HttpStatus.NOT_FOUND; // 404
        }

        ApiResponse errorResponse = new ApiResponse(false, message);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex, WebRequest request) {
        // Lỗi 500 chung
        ex.printStackTrace();
        ApiResponse errorResponse = new ApiResponse(false, "Đã có lỗi không mong muốn xảy ra: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}