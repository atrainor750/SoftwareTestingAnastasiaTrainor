package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeliveryPaths {
    @JsonProperty("droneId")
private String droneId;

    @JsonProperty("deliveries")
    private List<DeliveryEntry> deliveries;
}
