package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Query {
    @NotNull
    @JsonProperty("attribute")
    private String attribute;

    @NotNull
    @JsonProperty("operator")
    private String operator;

    @NotNull
    @JsonProperty("value")
    private String value;


}
