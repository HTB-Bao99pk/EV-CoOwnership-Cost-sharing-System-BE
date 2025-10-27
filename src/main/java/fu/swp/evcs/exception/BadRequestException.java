package fu.swp.evcs.exception;

/**
 * Exception ném ra khi request không hợp lệ
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
