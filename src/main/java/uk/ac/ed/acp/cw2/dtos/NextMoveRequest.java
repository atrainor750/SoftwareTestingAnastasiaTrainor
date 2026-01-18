package uk.ac.ed.acp.cw2.dtos;



// (imports not shown)
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NextMoveRequest {
    @Valid
    @NotNull
    @JsonProperty("start")
    private Position start;

    @Valid
    @NotNull
    @JsonProperty("angle")
    private Double angle;
}
