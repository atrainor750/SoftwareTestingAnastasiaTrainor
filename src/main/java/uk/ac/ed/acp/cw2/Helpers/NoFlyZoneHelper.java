
package uk.ac.ed.acp.cw2.Helpers;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.dtos.*;

        import java.util.ArrayList;
import java.util.List;



import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.acp.cw2.dtos.NoFlyZone;
import uk.ac.ed.acp.cw2.dtos.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class NoFlyZoneHelper {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String ilpEndpoint;

    /**
     *
     * Cached copy of the current no-fly zones.
     * Call refreshZones() once at the start of each endpoint to reload.
     */
    public List<NoFlyZone> zones = new ArrayList<>();

    public NoFlyZoneHelper(String ilpEndpoint) {
        this.ilpEndpoint = ilpEndpoint;
    }

    public Region[] getNoFlyZones() {
        String url = ilpEndpoint + "restricted-areas";
        return restTemplate.getForObject(url, Region[].class);
    }

    // ===============================================================
    // LOADING FROM ILP SERVICE
    // ===============================================================

    /**
     * Reloads the list of no-fly zones from the ILP REST service.
     * Call this once at the start of calcDeliveryPath / calcDeliveryPathAsGeoJson.
     */
    public void refreshZones() {
        try {

            String url = ilpEndpoint + "/restricted-areas";

            NoFlyZone[] response = restTemplate.getForObject(url, NoFlyZone[].class);
            if (response != null) {
                zones = new ArrayList<>(Arrays.asList(response));
            } else {
                zones = new ArrayList<>();
            }
        } catch (RestClientException e) {
            // On error, fail gracefully with an empty list
            e.printStackTrace();
            zones = new ArrayList<>();
        }
    }

    /**
     * Returns an immutable view of the current zones.
     * (Assumes refreshZones() has been called already.)
     */

    // ===============================================================
    // POINT & SEGMENT CHECKING
    // ===============================================================

    public boolean pointInNoFlyZone(Position p) {
        for (NoFlyZone z : zones) {
            // NoFlyZone DTO should have "coordinates" -> List<Position>
            if (pointInPolygon(p, z.getVertices())) {
                return true;
            }
        }
        return false;
    }

    public boolean edgeCutsNoFlyZone(Position a, Position b) {
        for (NoFlyZone z : zones) {
            if (segmentIntersectsPolygon(a, b, z.getVertices())) {
                return true;
            }
        }
        return false;
    }

    // ---- point in polygon ----
    private boolean pointInPolygon(Position p, List<Position> poly) {
        if (poly == null || poly.size() < 3) return false;

        boolean inside = false;
        int j = poly.size() - 1;

        for (int i = 0; i < poly.size(); i++) {
            double xi = poly.get(i).getLng();
            double yi = poly.get(i).getLat();
            double xj = poly.get(j).getLng();
            double yj = poly.get(j).getLat();

            boolean intersect = ((yi > p.getLat()) != (yj > p.getLat()))
                    && (p.getLng() < (xj - xi) * (p.getLat() - yi) / (yj - yi) + xi);

            if (intersect) inside = !inside;
            j = i;
        }

        return inside;
    }

    // ---- segment intersection ----
    private boolean segmentIntersectsPolygon(Position a, Position b, List<Position> poly) {
        if (poly == null || poly.size() < 2) return false;

        for (int i = 0; i < poly.size(); i++) {
            Position c = poly.get(i);
            Position d = poly.get((i + 1) % poly.size());
            if (segmentsIntersect(a, b, c, d)) return true;
        }
        return false;
    }

    private boolean segmentsIntersect(Position a, Position b, Position c, Position d) {
        return ccw(a, c, d) != ccw(b, c, d)
                && ccw(a, b, c) != ccw(a, b, d);
    }

    private boolean ccw(Position a, Position b, Position c) {
        return (c.getLat() - a.getLat()) * (b.getLng() - a.getLng()) >
                (b.getLat() - a.getLat()) * (c.getLng() - a.getLng());
    }
}

