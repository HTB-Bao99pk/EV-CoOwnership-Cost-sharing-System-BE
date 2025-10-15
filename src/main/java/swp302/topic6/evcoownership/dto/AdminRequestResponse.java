package swp302.topic6.evcoownership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO cho Admin xem danh sách yêu cầu tham gia nhóm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRequestResponse {
    private Long id;
    private Long groupId;
    private String groupName;
    private Long userId;
    private String userName;
    private String userEmail;
    private String message;
    private String status; // pending, approved, rejected
    private Date createdAt;
    private Date updatedAt;
    private Double requestedPercentage;
}
