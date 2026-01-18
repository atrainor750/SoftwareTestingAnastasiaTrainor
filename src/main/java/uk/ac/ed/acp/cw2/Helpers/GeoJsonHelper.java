package uk.ac.ed.acp.cw2.Helpers;



import uk.ac.ed.acp.cw2.dtos.*;

import java.util.*;

public class GeoJsonHelper {

    /** Builds ONE LineString containing the full flight path for the drone */
    public static Map<String, Object> buildSingleLineString(DeliveryPaths dp) {

        Map<String, Object> feature = new LinkedHashMap<>();
        feature.put("type", "Feature");

        // properties (optional)
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("droneId", dp.getDroneId());
        feature.put("properties", props);

        // geometry
        Map<String, Object> geom = new LinkedHashMap<>();
        geom.put("type", "LineString");

        List<List<Double>> coords = new ArrayList<>();

        // flatten all segments into a single coordinate list
        for (DeliveryEntry entry : dp.getDeliveries()) {
            for (Position p : entry.getFlightPath()) {
                coords.add(List.of(p.getLng(), p.getLat()));
            }
        }

        geom.put("coordinates", coords);
        feature.put("geometry", geom);

        return feature;
    }
}

