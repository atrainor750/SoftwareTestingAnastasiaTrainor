package uk.ac.ed.acp.cw2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.acp.cw2.dtos.Drone;
import uk.ac.ed.acp.cw2.service.DroneService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class Cw2Controller {

    private final DroneService droneService;

    public Cw2Controller(DroneService droneService) {
        this.droneService = droneService;
    }



    @GetMapping("/dronesWithCooling/{state}")
    public List<String> dronesWithCooling(@PathVariable boolean state){

        return droneService.dronesWithCooling(state);

    }
    @GetMapping("/droneDetails/{id}")
    public Drone droneDetails(@PathVariable String id){
        return droneService.droneDetails(id);
    }
    @GetMapping("/queryAsPath/{attributeName}/{attributeValue}")
    public List<String> queryAsPath(@PathVariable String attributeName, @PathVariable String attributeValue){
        return droneService.queryAsPath(attributeName, attributeValue);
    }

}


