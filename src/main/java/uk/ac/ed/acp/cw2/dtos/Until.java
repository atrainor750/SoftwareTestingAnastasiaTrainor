package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Until {
    @JsonProperty("hour")
    private int hour;

    @JsonProperty("minute")
    private int minute;

    @JsonProperty("second")
    private int second;

    @JsonProperty("nano")
    private int nano;
}
