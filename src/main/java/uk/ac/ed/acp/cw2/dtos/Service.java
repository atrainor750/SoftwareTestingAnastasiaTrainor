package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Service {
    @JsonProperty("servicePointId")
            private int servicePointId;
    @JsonProperty("drones")
            private List<Drones> drones;




}
