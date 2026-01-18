package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
// dto for capability class, contains all attributes of the capabilties of the drone

@Getter
@Setter
public class Capability {


   @JsonProperty("cooling")
    private boolean cooling;


   @JsonProperty("heating")
   private boolean heating;



    @JsonProperty("capacity")
    private Double capacity;


    @JsonProperty("maxMoves")
    private int maxMoves;


    @JsonProperty("costPerMove")
    private Double costPerMove;


    @JsonProperty("costInitial")
    private Double costInitial;


    @JsonProperty("costFinal")
    private Double costFinal;




}
