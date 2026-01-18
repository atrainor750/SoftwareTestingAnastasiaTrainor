package uk.ac.ed.acp.cw2.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor

public class InRegionRequest {
    @Valid
    @NotNull
    @JsonProperty("position")
    private Position position;

    @Valid
    @NotNull
    @JsonProperty("region")
    private Region region;
}
