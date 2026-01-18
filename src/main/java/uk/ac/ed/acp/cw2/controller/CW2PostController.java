package uk.ac.ed.acp.cw2.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.acp.cw2.dtos.CalcDeliveryPathsResponse;
import uk.ac.ed.acp.cw2.dtos.Drone;
import uk.ac.ed.acp.cw2.dtos.MedDispatchRecord;
import uk.ac.ed.acp.cw2.dtos.Query;
import uk.ac.ed.acp.cw2.service.DroneService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CW2PostController {

    private final DroneService droneService;

    public CW2PostController(DroneService droneService) {
        this.droneService = droneService;
    }
    @PostMapping("/query")
    public List<String> query(@Valid @RequestBody List<Query> queryList) {
return droneService.query(queryList);
    }
    @PostMapping("/queryAvailableDrones")
    public List<String> queryAvailableDrones(@Valid @RequestBody List<MedDispatchRecord> recordList) {
        return  droneService.queryAvailableDrones(recordList);
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/calcDeliveryPath")
    public CalcDeliveryPathsResponse calcDeliveryPath(@Valid @RequestBody List<MedDispatchRecord> recordList){
        return  droneService.calcDeliveryPath(recordList);
    }
    @PostMapping(
            value = "/calcDeliveryPathAsGeoJson",
            consumes = "application/json",
            produces = "application/json"
    )
    public Object calcDeliveryPathAsGeoJson(@RequestBody List<MedDispatchRecord> records) {
        return droneService.calcDeliveryPathAsGeoJson(records);
    }

}