package uk.ac.ed.acp.cw2.Helpers;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.dtos.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Helper {

    private final DroneHelper droneHelper;

    // Constructor injection (Spring will inject DroneHelper here)
    public Helper(DroneHelper droneHelper) {
        this.droneHelper = droneHelper;
    }

    // ---------------------------
    // Inner helper class
    // ---------------------------
    public static class Point {
        public double x;
        public double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // Convert positions -> points
    public static List<Point> convertToPoints(List<Position> positions) {
        return positions.stream()
                .map(p -> new Point(p.getLng(), p.getLat()))
                .toList();
    }

    // Polygon edge check
    public static boolean isOnPolygonEdge(Point point, List<Point> polygon, double eps) {
        for (int i = 0; i < polygon.size() - 1; i++) {
            Point a = polygon.get(i);
            Point b = polygon.get(i + 1);
            if (isPointOnSegment(a, b, point, eps)) {
                return true;
            }
        }
        return false;
    }

    static boolean isPointOnSegment(Point a, Point b, Point p, double eps) {
        double abx = b.x - a.x, aby = b.y - a.y;
        double apx = p.x - a.x, apy = p.y - a.y;
        double ab_len2 = abx * abx + aby * aby;

        if (ab_len2 < eps * eps) {
            return Math.hypot(apx, apy) <= eps;
        }

        double t = (apx * abx + apy * aby) / ab_len2;

        if (t < -eps || t > 1 + eps) {
            return false;
        }

        double cross = Math.abs(abx * apy - aby * apx);
        double dist = cross / Math.sqrt(ab_len2);

        return dist <= eps;
    }

    public static boolean isValid(Position position){
        Double lat = position.getLat();
        Double lng = position.getLng();
        return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
    }

    // ---------------------------
    // Availability map builder
    // ---------------------------
    public Map<String, List<Availability>> buildAvailabilityMap() {

        List<DronesForServicePoint> servicePoints = droneHelper.getDronesForServicePoints();
        Map<String, List<Availability>> availabilityMap = new HashMap<>();

        for (DronesForServicePoint sp : servicePoints) {
            for (DroneAvailabilityEntry d : sp.getDrones()) {
                availabilityMap.put(d.getId(), d.getAvailability());
            }
        }

        return availabilityMap;
    }

    public Map<Integer, Position> buildServicePointLocationMap() {
        List<ServicePoint> servicePoints = droneHelper.getServicePoints();
        Map<Integer, Position> servicePointLocationMap = new HashMap<>();
        for(ServicePoint sp : servicePoints) {
            servicePointLocationMap.put(sp.getId(), sp.getLocation());
        }
        return servicePointLocationMap;
    }
    public String coordKey(Position p) {
        return p.getLng() + "," + p.getLat();
    }

    public String legKey(Position a, Position b) {
        return coordKey(a) + "->" + coordKey(b);
    }

    public boolean meetsStaticRequirements(MedDispatchRecord rec, Drone drone) {
        if (rec.getRequirements() == null) return true;

        Requirements req = rec.getRequirements();
        Capability cap = drone.getCapability();

        // capacity (required <= drone capacity)
        if (req.getCapacity() != null && cap.getCapacity() < req.getCapacity()) {
            return false;
        }

        // cooling (if needed, drone must support)
        if (Boolean.TRUE.equals(req.getCooling()) && !cap.isCooling()) {
            return false;
        }

        // heating (if needed, drone must support)
        if (Boolean.TRUE.equals(req.getHeating()) && !cap.isHeating()) {
            return false;
        }

        return true;
    }

    /**
     * Availability per day-of-week.
     * Adjust this to match your Availability DTO structure.
     */
    public boolean isDroneAvailableForDay(LocalDate date, List<Availability> slots) {
        // If no availability data, treat as always available (safer default)
        if (slots == null || slots.isEmpty()) {
            return true;
        }

        DayOfWeek dow = date.getDayOfWeek();

        for (Availability a : slots) {
            DayOfWeek availDow = a.getDayOfWeek();
            if (availDow != null && availDow == dow) {
                return true;
            }
        }
        return false;
    }

    public boolean isDroneAvailableFor(LocalDate date,
                                       LocalTime time,
                                       List<Availability> slots) {
        // If no availability data, treat as always available
        if (slots == null || slots.isEmpty()) {
            return true;
        }

        DayOfWeek dow = date.getDayOfWeek();

        for (Availability a : slots) {
            if (a == null || a.getDayOfWeek() == null) continue;

            if (a.getDayOfWeek().equals(dow)) {
                // If from/until are null, treat as full-day availability
                LocalTime from  = a.getFrom()  != null ? a.getFrom()  : LocalTime.MIN;
                LocalTime until = a.getUntil() != null ? a.getUntil() : LocalTime.MAX;

                // inclusive start, exclusive end
                if (!time.isBefore(from) && time.isBefore(until)) {
                    return true;
                }
            }
        }

        return false;
    }


}










