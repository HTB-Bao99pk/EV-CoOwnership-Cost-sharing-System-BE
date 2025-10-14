package swp302.topic6.evcoownership.dto;

public class CreateGroupRequest {
    private Long vehicleId;
    private String groupName;
    private String description;
    private Double estimatedValue;
    private Integer maxMembers;
    private Double minOwnershipPercentage; // percent, e.g., 5.0 for

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(Double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public Double getMinOwnershipPercentage() {
        return minOwnershipPercentage;
    }

    public void setMinOwnershipPercentage(Double minOwnershipPercentage) {
        this.minOwnershipPercentage = minOwnershipPercentage;
    }
}
