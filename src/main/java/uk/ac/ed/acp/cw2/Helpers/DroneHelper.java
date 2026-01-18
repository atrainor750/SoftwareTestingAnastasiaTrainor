package uk.ac.ed.acp.cw2.Helpers;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.acp.cw2.dtos.Drone;
import uk.ac.ed.acp.cw2.dtos.DronesForServicePoint;
import uk.ac.ed.acp.cw2.dtos.Position;
import uk.ac.ed.acp.cw2.dtos.ServicePoint;

import java.util.Arrays;
import java.util.List;

@Component
public class DroneHelper {

    private final String ilpEndpoint;


    public DroneHelper(String ilpEndpoint) {
        this.ilpEndpoint = ilpEndpoint;
    }

    public List<Drone> getAllDrones() {
        String url = ilpEndpoint + "/drones";
        RestTemplate restTemplate = new RestTemplate();
        Drone[] dronesArray = restTemplate.getForObject(url, Drone[].class);
        return Arrays.asList(dronesArray);
    }
    public List<DronesForServicePoint> getDronesForServicePoints() {
        String url = ilpEndpoint + "/drones-for-service-points";
        RestTemplate restTemplate = new RestTemplate();
        DronesForServicePoint[] arr = restTemplate.getForObject(url, DronesForServicePoint[].class);
        return Arrays.asList(arr);
    }
    public List<ServicePoint> getServicePoints() {
        String url = ilpEndpoint + "/service-points";
        RestTemplate restTemplate = new RestTemplate();
        ServicePoint[] arr = restTemplate.getForObject(url, ServicePoint[].class);
        return Arrays.asList(arr);
    }
    public String makeKey(Position a, Position b) {
        return a.getLng() + "," + a.getLat()
                + "->"
                + b.getLng() + "," + b.getLat();
    }


}

