package uk.ac.ed.acp.cw2.dtos;




import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LngLatPairRequest {
    @Valid
    @NotNull
    @JsonProperty("position1")
    private Position position1;
    @Valid
    @NotNull
    @JsonProperty("position2")
    private Position position2;
}
