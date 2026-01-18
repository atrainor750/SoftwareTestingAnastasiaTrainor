package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
//DTO for drone data containing capability, name and id number
@Getter
@Setter
public class Drone {

    @JsonProperty("name")
    private String name;


    @JsonProperty("id")
    private String id;


    @JsonProperty("capability")
    private Capability capability;
}
