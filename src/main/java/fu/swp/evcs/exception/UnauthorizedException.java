package fu.swp.evcs.exception;

/**
 * Exception ném ra khi người dùng chưa đăng nhập hoặc không có quyền
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
