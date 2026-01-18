package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class Deliveries {

    @JsonProperty("delivery id")
    private int deliveryId;

    @JsonProperty("flight path")
    private List<Position> flightPath;
}
