<<<<<<<< HEAD:src/main/java/fu/swp/evcs/entity/VoteResponse.java
package fu.swp.evcs.entity;
========
package swp302.topic6.evcoownership.dto;
>>>>>>>> 8dae5e874197d1b4e79bd3be688ad9832ad26e91:src/main/java/fu/swp/evcs/dto/VoteResponse.java

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vote_responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vote_id", nullable = false)
    private Long voteId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String response; // "YES" hoặc "NO"

    @Column(name = "voted_at")
    @Builder.Default
    private LocalDateTime votedAt = LocalDateTime.now();
}
