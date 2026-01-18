package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CalcDeliveryPathsResponse {
    @JsonProperty("totalCost")
    private Double totalCost;

    @JsonProperty("totalMoves")
    private int totalMoves;

    @JsonProperty("dronePaths")
    private List<DeliveryPaths> dronePaths;
}
