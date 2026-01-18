package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeliveryEntry {

        @JsonProperty("deliveryId")
        private int deliveryId;

        @JsonProperty("flightPath")
        private List<Position> flightPath;
    }


