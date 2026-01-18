package uk.ac.ed.acp.cw2.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor

public class Region {
    @Valid
    @NotNull
    @JsonProperty("name")
    private String name;

    @Valid
    @NotNull
    @JsonProperty("vertices")
    private List<Position> vertices;
}

