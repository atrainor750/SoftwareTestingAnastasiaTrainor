package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Drones {
    @JsonProperty("id")
    private String id;

    @JsonProperty("availability")
    private Availability availability;
}
