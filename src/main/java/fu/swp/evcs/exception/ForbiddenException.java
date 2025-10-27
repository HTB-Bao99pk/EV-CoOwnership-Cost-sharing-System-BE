package fu.swp.evcs.exception;

/**
 * Exception ném ra khi người dùng không có quyền truy cập tài nguyên
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
