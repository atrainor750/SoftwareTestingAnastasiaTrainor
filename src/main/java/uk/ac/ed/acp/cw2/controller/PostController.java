package uk.ac.ed.acp.cw2.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ed.acp.cw2.dtos.*;
import uk.ac.ed.acp.cw2.dtos.InRegionRequest;
import uk.ac.ed.acp.cw2.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;



@RestController
@RequestMapping("/api/v1")
public class PostController {
    @Autowired
    private final RestService regionService;
    private final RestService restService;


    public PostController(RestService regionService, RestService restService) {
        this.regionService = regionService;
        this.restService = restService;
    }

    @PostMapping("/distanceTo")
    public double distanceTo(@Valid @RequestBody LngLatPairRequest request) {
        return regionService.distanceTo(request);
    }

    @PostMapping("/isCloseTo")
    public boolean isCloseTo(@Valid @RequestBody LngLatPairRequest request) {
        return regionService.isCloseTo(request);
    }

    @PostMapping("/nextPosition")
    public Position nextPosition(@Valid @RequestBody NextMoveRequest request) {
        return regionService.nextPosition(request);
    }

    @PostMapping("/isInRegion")
    public boolean isInRegion(@Valid @RequestBody InRegionRequest request) {
        return regionService.isInRegion(request);
    }
}


