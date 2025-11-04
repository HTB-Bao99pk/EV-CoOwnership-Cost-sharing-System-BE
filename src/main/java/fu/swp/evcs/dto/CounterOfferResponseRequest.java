package fu.swp.evcs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounterOfferResponseRequest {

    /**
     * true = accept counter offer
     * false = reject counter offer
     */
    private Boolean accept;
}

