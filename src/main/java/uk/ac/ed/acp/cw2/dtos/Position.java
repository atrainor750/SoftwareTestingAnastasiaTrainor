package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor

@Getter
@Setter
public class Position {
    @Valid
    @NotNull
    @JsonProperty("lng")
    private Double lng;

    @NotNull
    @JsonProperty("lat")
    private Double lat;
}

