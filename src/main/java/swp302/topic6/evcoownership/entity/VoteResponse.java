package swp302.topic6.evcoownership.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Vote_Responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @Column(name = "vote_id", nullable = false)
    private Long voteId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String response; // "YES" hoặc "NO"

    @Column(name = "voted_at")
    private LocalDateTime votedAt = LocalDateTime.now();
}
