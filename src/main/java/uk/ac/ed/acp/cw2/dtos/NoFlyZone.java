package uk.ac.ed.acp.cw2.dtos;



import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@Getter
@Setter
public class NoFlyZone {

    @JsonProperty("name")
    private String name;   // optional - helps identify each zone

    @JsonProperty("vertices")
    private List<Position> vertices;


}


