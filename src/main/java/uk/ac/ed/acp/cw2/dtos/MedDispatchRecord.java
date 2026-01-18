package uk.ac.ed.acp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedDispatchRecord {

        @JsonProperty("id")
        private int id;

        @JsonProperty("date")
        private String date;

        @JsonProperty("time")
        private String time;

        @JsonProperty("requirements")
        private Requirements requirements;

        @JsonProperty("delivery")
        private Position delivery;


    }

