package swp302.topic6.evcoownership.dto;

import java.math.BigDecimal;

public class GroupSettingsRequest {
    private Integer maxMembers;
    private BigDecimal minOwnershipPercentage;

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public BigDecimal getMinOwnershipPercentage() {
        return minOwnershipPercentage;
    }

    public void setMinOwnershipPercentage(BigDecimal minOwnershipPercentage) {
        this.minOwnershipPercentage = minOwnershipPercentage;
    }
}
