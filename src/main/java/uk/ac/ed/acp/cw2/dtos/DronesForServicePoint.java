package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class DronesForServicePoint {

    @JsonProperty("servicePointId")
    private int servicePointId;

    @JsonProperty("drones")
    private List<DroneAvailabilityEntry> drones;
}


