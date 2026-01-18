package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DronePaths {
    @JsonProperty("drone Id")
    private String droneId;

    @JsonProperty("deliveries")
    private List<Deliveries> deliveries;
}
