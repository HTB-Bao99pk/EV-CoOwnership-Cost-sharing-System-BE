package fu.swp.evcs.exception;

/**
 * Exception ném ra khi không tìm thấy resource (User, Group, Vehicle...)
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
