package uk.ac.ed.acp.cw2.service;

import uk.ac.ed.acp.cw2.dtos.CalcDeliveryPathsResponse;
import uk.ac.ed.acp.cw2.dtos.Drone;
import uk.ac.ed.acp.cw2.dtos.MedDispatchRecord;
import uk.ac.ed.acp.cw2.dtos.Query;
import java.util.List;
// top layer of drone service implementation, what is passed to the logic of everything to do with drones

public interface DroneService {

    List<String> dronesWithCooling(boolean state);

    Drone droneDetails(String id);
   List<String> queryAsPath(String attributeName,  String attributeValue);
     List<String> query(List<Query> queryList);
     List<String> queryAvailableDrones(List<MedDispatchRecord> recordList);
    CalcDeliveryPathsResponse calcDeliveryPath(List<MedDispatchRecord> recordList);
     Object calcDeliveryPathAsGeoJson(List<MedDispatchRecord> records);



    List<Drone> getAllDrones();   // internal helper for reuse
}
