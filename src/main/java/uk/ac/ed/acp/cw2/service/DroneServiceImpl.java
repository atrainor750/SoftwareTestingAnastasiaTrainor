package uk.ac.ed.acp.cw2.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.Helpers.*;
import uk.ac.ed.acp.cw2.Helpers.Helper;


import java.time.LocalTime;
import java.util.Map;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.time.LocalDate;


import uk.ac.ed.acp.cw2.dtos.*;

import java.time.DayOfWeek;
import java.util.*;

import static java.lang.Double.valueOf;
import static org.apache.coyote.http11.Constants.a;

@Service
@Component
public class DroneServiceImpl implements DroneService {




    private final DroneHelper droneHelper;
    private final Helper helper;
    private final NoFlyZoneHelper noFlyZoneHelper;
    private final Helper availabilityHelper;

    @Autowired
    public DroneServiceImpl(DroneHelper droneHelper, Helper helper, NoFlyZoneHelper noFlyZoneHelper, Helper availabilityHelper ) {


        this.droneHelper = droneHelper;
        this.helper = helper;
        this.noFlyZoneHelper = noFlyZoneHelper;
        this.availabilityHelper = availabilityHelper;
    }


    private double distance(Position a, Position b) {
        double dx = a.getLng() - b.getLng();
        double dy = a.getLat() - b.getLat();
        return Math.sqrt(dx * dx + dy * dy);
    }





    // makes a list of all the drones
    @Override
    public List<Drone> getAllDrones() {
        return droneHelper.getAllDrones();
    }

    @Override
    //logic of finding the cooling state of the drones, matches it with the input parameter and returns list
    public List<String> dronesWithCooling(boolean state) {
        List<Drone> allDrones = droneHelper.getAllDrones();
        List<String> result = new ArrayList<>();

        for (Drone allDrone : allDrones) {
            boolean currentState = allDrone.getCapability().isCooling();
// match given state to that of the current drone of allDrones
            if (state == currentState) {
                result.add(allDrone.getId());
            }
        }

        return result;
    }

    public Drone droneDetails(String id) {
        List<Drone> allDrones = droneHelper.getAllDrones();
        for (Drone allDrone : allDrones) {
            if (allDrone.getId().equals(id)) {
                return allDrone;
            }

        }
        return null;
    }

    public List<String> queryAsPath(String attributeName, String attributeValue) {
        List<Drone> allDrones = droneHelper.getAllDrones();
        List<String> result = new ArrayList<>();
        for (Drone allDrone : allDrones) {
            switch (attributeName) {
                case "id":
                    String id = allDrone.getId();
                    if (attributeValue.equals(id)) {
                        result.add(allDrone.getId());
                    }
                    break;
                case "cooling":
                    boolean cooling = allDrone.getCapability().isCooling();
                    String coolingString = cooling ? "true" : "false";
                    if (attributeValue.equals(coolingString)) {
                        result.add(allDrone.getId());
                    }
                    break;
                case "capacity":
                    Double capacity = allDrone.getCapability().getCapacity();
                    if (attributeValue.equals(capacity.toString())) {
                        result.add(allDrone.getId());
                    }
                    break;
                case "heating":
                    boolean heating = allDrone.getCapability().isHeating();
                    String heatingString = heating ? "true" : "false";
                    if (attributeValue.equals(heatingString)) {
                        result.add(allDrone.getId());
                    }
                    break;
                case "maxMoves":
                    int maxMoves = allDrone.getCapability().getMaxMoves();
                    Integer maxMovesInt = maxMoves;
                    if (attributeValue.equals(maxMovesInt.toString())) {
                        result.add(allDrone.getId());
                    }
                    break;
                case "costPerMove":
                    Double costPerMove = allDrone.getCapability().getCostPerMove();
                    if (attributeValue.equals(costPerMove.toString())) {
                        result.add(allDrone.getId());
                    }
                    break;
                case "costFinal":
                    Double costFinal = allDrone.getCapability().getCostFinal();
                    if (attributeValue.equals(costFinal.toString())) {
                        result.add(allDrone.getId());
                    }
                    break;
                case "costInitial":
                    Double costInitial = allDrone.getCapability().getCostInitial();
                    if (attributeValue.equals(costInitial.toString())) {
                        result.add(allDrone.getId());
                    }
                    break;
                case "name":
                    String name = allDrone.getName();
                    if (attributeValue.equals(name)) {
                        result.add(allDrone.getId());
                    }
                    break;
                default:
                    break;


            }


        }


        return result;
    }

    public List<String> query(List<Query> queryList) {
        List<Drone> allDrones = droneHelper.getAllDrones();
        List<String> result = new ArrayList<>();

        for (Drone drone : allDrones) {
            boolean flag1 = true;
            for (Query query : queryList) {
                String attribute = query.getAttribute();
                String value = query.getValue();
                String operator = query.getOperator();


                switch (attribute) {
                    case "cooling":
                        boolean valueD = drone.getCapability().isCooling();
                        String valueDS = valueD ? "true" : "false";
                        if (operator.equals("=")) {
                            if (!valueDS.equals(value)) {
                                flag1 = false;
                            }
                        }
                        if (operator.equals("!=")) {
                            if (valueDS.equals(value)) {
                                flag1 = false;
                            }
                        }


                        break;
                    case "heating":
                        boolean valueH = drone.getCapability().isHeating();
                        String valueHDS = valueH ? "true" : "false";
                        if (operator.equals("=")) {
                            if (!valueHDS.equals(value)) {
                                flag1 = false;
                            }
                        }
                        if (operator.equals("!=")) {
                            if (valueHDS.equals(value)) {
                                flag1 = false;
                            }
                        }
                        break;
                    case "capacity":
                        Double capacity = drone.getCapability().getCapacity();
                        Double valueC = Double.valueOf(value);
                        switch (operator) {
                            case "=":
                                if (!valueC.equals(capacity)) {
                                    flag1 = false;
                                }
                                break;
                            case "!=":
                                if (valueC.equals(capacity)) {
                                    flag1 = false;
                                }
                                break;
                            case ">":
                                if (capacity <= valueC) {
                                    flag1 = false;


                                }
                                break;
                            case "<":
                                if (capacity >= valueC) {
                                    flag1 = false;
                                }
                        }
                        break;
                    case "maxMoves":
                        int maxMoves = drone.getCapability().getMaxMoves();
                        int valueMM = Integer.parseInt(value);
                        switch (operator) {
                            case "=":
                                if (valueMM != maxMoves) {
                                    flag1 = false;
                                }
                                break;
                            case "!=":
                                if (valueMM == maxMoves) {
                                    flag1 = false;
                                }
                                break;
                            case ">":
                                if (maxMoves <= valueMM) {
                                    flag1 = false;
                                }
                                break;
                            case "<":
                                if (maxMoves >= valueMM) {
                                    flag1 = false;
                                }
                                break;
                        }

                        break;

                    case "costPerMove":
                        Double costPerMove = drone.getCapability().getCostPerMove();
                        Double valuePM = Double.valueOf(value);
                        switch (operator) {
                            case "=":
                                if (!valuePM.equals(costPerMove)) {
                                    flag1 = false;
                                }
                                break;
                            case "!=":
                                if (valuePM.equals(costPerMove)) {
                                    flag1 = false;
                                }
                                break;
                            case ">":
                                if (costPerMove <= valuePM) {
                                    flag1 = false;
                                }
                                break;
                            case "<":
                                if (costPerMove >= valuePM) {
                                    flag1 = false;
                                }
                                break;
                        }
                        break;


                    case "costInitial":
                        Double costInitial = drone.getCapability().getCostInitial();
                        Double valueInitial = Double.valueOf(value);
                        switch (operator) {
                            case "=":
                                if (!valueInitial.equals(costInitial)) {
                                    flag1 = false;
                                }
                                break;
                            case "!=":
                                if (valueInitial.equals(costInitial)) {
                                    flag1 = false;
                                }
                                break;
                            case ">":
                                if (costInitial <= valueInitial) {
                                    flag1 = false;
                                }
                                break;
                            case "<":
                                if (costInitial >= valueInitial) {
                                    flag1 = false;
                                }
                        }

                        break;
                    case "costFinal":
                        Double costFinal = drone.getCapability().getCostFinal();
                        Double valueFinal = Double.valueOf(value);
                        switch (operator) {
                            case "=":
                                if (!valueFinal.equals(costFinal)) {
                                    flag1 = false;
                                }
                                break;
                            case "!=":
                                if (valueFinal.equals(costFinal)) {
                                    flag1 = false;
                                }
                                break;
                            case ">":
                                if (costFinal <= valueFinal) {
                                    flag1 = false;
                                }
                                break;
                            case "<":
                                if (costFinal >= valueFinal) {
                                    flag1 = false;
                                }
                                break;
                        }

                }


            }
            if (flag1) {


                result.add(drone.getId());
            }

        }


        return result;
    }

    public List<String> queryAvailableDrones(List<MedDispatchRecord> recordList) {


        List<Drone> allDrones = droneHelper.getAllDrones();
        List<String> result = new ArrayList<>();

        Map<String, List<Availability>> availabilityMap = helper.buildAvailabilityMap();
        List<DronesForServicePoint> sps = droneHelper.getDronesForServicePoints();
        Map<Integer, Position> servicePointLocationMap = helper.buildServicePointLocationMap();


        Map<String, Position> droneStartPoint = new HashMap<>();

        for (DronesForServicePoint sp : sps) {
            Position spPos = servicePointLocationMap.get(sp.getServicePointId());
            for (DroneAvailabilityEntry da : sp.getDrones()) {
                droneStartPoint.put(da.getId(), spPos);
            }
        }


        Map<LocalDate, List<MedDispatchRecord>> recordsByDay =
                recordList.stream()
                        .collect(Collectors.groupingBy(
                                rec -> LocalDate.parse(rec.getDate())
                        ));



        for (Drone drone : allDrones) {

            Position start = droneStartPoint.get(drone.getId());
            if (start == null) {
                continue;
            }

            boolean droneMatchesAll = true;


            for (MedDispatchRecord record : recordList) {

                double droneCap = drone.getCapability().getCapacity();
                double reqCap = record.getRequirements().getCapacity();

                if (droneCap < reqCap) {
                    droneMatchesAll = false;
                    break;
                }

                boolean reqCooling = Boolean.TRUE.equals(record.getRequirements().getCooling());
                boolean reqHeating = Boolean.TRUE.equals(record.getRequirements().getHeating());

                if (reqCooling && !drone.getCapability().isCooling()) {
                    droneMatchesAll = false;
                    break;
                }

                if (reqHeating && !drone.getCapability().isHeating()) {
                    droneMatchesAll = false;
                    break;
                }
            }

            if (!droneMatchesAll) continue;



            List<Availability> availList = availabilityMap.get(drone.getId());
            if (availList == null || availList.isEmpty()) continue;

            boolean availableForAllDays = true;

            for (LocalDate day : recordsByDay.keySet()) {

                DayOfWeek dow = day.getDayOfWeek();

                boolean availableThisDay = false;

                for (Availability av : availList) {
                    if (av.getDayOfWeek() == dow) {
                        availableThisDay = true;
                        break;
                    }
                }

                if (!availableThisDay) {
                    availableForAllDays = false;
                    break;
                }
            }

            if (!availableForAllDays) continue;



            boolean droneCanFlyAllDays = true;

            for (Map.Entry<LocalDate, List<MedDispatchRecord>> entry : recordsByDay.entrySet()) {

                LocalDate day = entry.getKey();
                List<MedDispatchRecord> todaysRecords = entry.getValue();

                // DAILY RESET
                double totalMoves = 0;
                double stepSize = 0.00015;
                Position currentPosition = start;

                // build today's delivery list
                List<Position> todaysDeliveries = todaysRecords.stream()
                        .map(MedDispatchRecord::getDelivery)
                        .collect(Collectors.toList());

                // greedy routing
                while (!todaysDeliveries.isEmpty()) {

                    Position nearest = null;
                    double minDist = Double.MAX_VALUE;

                    for (Position del : todaysDeliveries) {
                        double dx = del.getLng() - currentPosition.getLng();
                        double dy = del.getLat() - currentPosition.getLat();
                        double dist = Math.sqrt(dx * dx + dy * dy);

                        if (dist < minDist) {
                            minDist = dist;
                            nearest = del;
                        }
                    }

                    double moves = minDist / stepSize;
                    totalMoves += moves;

                    if (totalMoves > drone.getCapability().getMaxMoves()) {
                        droneCanFlyAllDays = false;
                        break;
                    }

                    currentPosition = nearest;
                    todaysDeliveries.remove(nearest);
                }

                if (!droneCanFlyAllDays) break;

                // return to service point
                double dx = start.getLng() - currentPosition.getLng();
                double dy = start.getLat() - currentPosition.getLat();
                totalMoves += Math.sqrt(dx * dx + dy * dy) / stepSize;

                if (totalMoves > drone.getCapability().getMaxMoves()) {
                    droneCanFlyAllDays = false;
                    break;
                }

                // DAILY COST
                double totalCost =
                        drone.getCapability().getCostInitial() +
                                totalMoves * drone.getCapability().getCostPerMove() +
                                drone.getCapability().getCostFinal();

                double distributed = totalCost / todaysRecords.size();

                for (MedDispatchRecord r : todaysRecords) {
                    Double maxC = r.getRequirements().getMaxCost();
                    if (maxC != null && distributed > maxC) {
                        droneCanFlyAllDays = false;
                        break;
                    }
                }

                if (!droneCanFlyAllDays) break;
            }

            if (!droneCanFlyAllDays) continue;

            // Passed everything!
            result.add(drone.getId());
        }

        return result;
    }

    @Override
    public CalcDeliveryPathsResponse calcDeliveryPath(List<MedDispatchRecord> recordList) {


            noFlyZoneHelper.refreshZones();





        CalcDeliveryPathsResponse resp = new CalcDeliveryPathsResponse();


            if (recordList == null || recordList.isEmpty()) {
                resp.setDronePaths(List.of());
                resp.setTotalCost(0.0);
                resp.setTotalMoves(0);
                return resp;
            }


            Map<LocalDate, List<MedDispatchRecord>> byDay = recordList.stream()
                    .collect(Collectors.groupingBy(r -> LocalDate.parse(r.getDate())));


            List<Drone> allDrones = droneHelper.getAllDrones();
            Map<String, Drone> droneMap = allDrones.stream()
                    .collect(Collectors.toMap(Drone::getId, d -> d));

            Map<Integer, Position> spPos = helper.buildServicePointLocationMap();
            Map<String, List<Availability>> availMap = helper.buildAvailabilityMap();


            Map<String, Position> droneStart = new HashMap<>();

            for (DronesForServicePoint sp : droneHelper.getDronesForServicePoints()) {
                Position p = spPos.get(sp.getServicePointId());
                if (p == null) continue;

                for (DroneAvailabilityEntry da : sp.getDrones()) {
                    droneStart.put(da.getId(), p);
                }
            }

            // If nothing loaded → bail
            if (droneStart.isEmpty()) {
                resp.setDronePaths(List.of());
                resp.setTotalMoves(0);
                resp.setTotalCost(0.0);
                return resp;
            }


            Position firstDelivery = recordList.get(0).getDelivery();

            List<String> orderedDroneIds = allDrones.stream()
                    .map(Drone::getId)
                    .filter(droneStart::containsKey) // only drones with service point
                    .sorted(Comparator.comparingDouble(
                            id -> distance(droneStart.get(id), firstDelivery)
                    ))
                    .collect(Collectors.toList());


            AStar aStar = new AStar(noFlyZoneHelper);

            Set<Integer> assignedDeliveries = new HashSet<>();
            List<DeliveryPaths> allDronePlans = new ArrayList<>();
            int totalMoves = 0;
            double totalCost = 0.0;

            // Sort days for deterministic behaviour
            List<LocalDate> days = new ArrayList<>(byDay.keySet());
            Collections.sort(days);

            for (String droneId : orderedDroneIds) {

                Drone drone = droneMap.get(droneId);
                Position startPoint = droneStart.get(droneId);

                DeliveryPaths plan = new DeliveryPaths();
                plan.setDroneId(droneId);

                List<DeliveryEntry> droneEntries = new ArrayList<>();
                int droneMoves = 0;
                double droneCost = 0.0;

                List<Availability> slots = availMap.get(droneId);

                // For each day, try to do as many dispatches as possible with this drone
                for (LocalDate day : days) {

                    // skip if all deliveries for this day are already assigned
                    List<MedDispatchRecord> todaysAll = byDay.get(day);
                    if (todaysAll == null) continue;

                    List<MedDispatchRecord> todaysUnassigned = todaysAll.stream()
                            .filter(r -> !assignedDeliveries.contains(r.getId()))
                            .collect(Collectors.toList());

                    if (todaysUnassigned.isEmpty()) continue;

                    // filter by capabilities AND actual availability (date+time)
                    List<MedDispatchRecord> feasible = todaysUnassigned.stream()
                            .filter(r -> helper.meetsStaticRequirements(r, drone))
                            .filter(r -> helper.isDroneAvailableFor(
                                    LocalDate.parse(r.getDate()),
                                    LocalTime.parse(r.getTime()),
                                    slots
                            ))
                            .collect(Collectors.toList());

                    if (feasible.isEmpty()) continue;

                    // ==========================
                    // PRECOMPUTE PATHS FOR THIS DAY & DRONE
                    // ==========================
                    Map<String, List<Position>> pathStore = new HashMap<>();
                    Map<String, Integer> moveStore = new HashMap<>();

                    // mapping from coordKey -> MedDispatchRecord
                    Map<String, MedDispatchRecord> recordByPosKey = new HashMap<>();
                    for (MedDispatchRecord r : feasible) {
                        recordByPosKey.put(helper.coordKey(r.getDelivery()), r);
                    }

                    List<Position> deliveryPositions = feasible.stream()
                            .map(MedDispatchRecord::getDelivery)
                            .collect(Collectors.toList());

                    // SP → each delivery
                    for (Position d : deliveryPositions) {
                        List<Position> p = aStar.findPath(startPoint, d);
                        String k = helper.legKey(startPoint, d);
                        pathStore.put(k, p);
                        moveStore.put(k, p.size() - 1);
                    }

                    // delivery → delivery
                    for (Position a : deliveryPositions) {
                        for (Position b : deliveryPositions) {
                            if (a == b) continue;
                            List<Position> p = aStar.findPath(a, b);
                            String k = helper.legKey(a, b);
                            pathStore.put(k, p);
                            moveStore.put(k, p.size() - 1);
                        }
                    }

                    // delivery → return SP
                    for (Position d : deliveryPositions) {
                        List<Position> p = aStar.findPath(d, startPoint);
                        String k = helper.legKey(d, startPoint);
                        pathStore.put(k, p);
                        moveStore.put(k, p.size() - 1);
                    }



                    List<Position> remaining = new ArrayList<>(deliveryPositions);
                    List<Position> ordered = new ArrayList<>();

                    Position current = startPoint;
                    int usedMovesThisFlight = 0;
                    int maxMoves = drone.getCapability().getMaxMoves();

                    while (!remaining.isEmpty()) {

                        Position best = null;
                        int bestGo = Integer.MAX_VALUE;

                        for (Position cand : remaining) {
                            String goKey = helper.legKey(current, cand);
                            String backKey = helper.legKey(cand, startPoint);

                            Integer go = moveStore.get(goKey);
                            Integer back = moveStore.get(backKey);
                            if (go == null || back == null) {

                                continue;
                            }

                            int needed = go + back;

                            // ensure we can still return to SP after this delivery
                            if (usedMovesThisFlight + needed > maxMoves) {

                                continue;
                            }

                            // softer maxCost (if present)
                            MedDispatchRecord rec = recordByPosKey.get(helper.coordKey(cand));
                            Double maxCostAllowed = null;
                            if (rec != null && rec.getRequirements() != null) {
                                maxCostAllowed = rec.getRequirements().getMaxCost();
                            }

                            if (maxCostAllowed != null) {
                                double init = drone.getCapability().getCostInitial();
                                double perMove = drone.getCapability().getCostPerMove();

                                // cost up to reaching this delivery (approx)
                                int predictedMoves = usedMovesThisFlight + go;
                                double predictedCost = init + perMove * predictedMoves;

                                if (predictedCost > maxCostAllowed) {
                                    // This delivery would break its maxCost constraint, skip it
                                    continue;
                                }
                            }

                            // greedy: smallest go cost
                            if (go < bestGo) {
                                bestGo = go;
                                best = cand;
                            }
                        }


                        if (best == null) {

                            break;
                        }

                        ordered.add(best);
                        remaining.remove(best);

                        int goMoves = moveStore.get(helper.legKey(current, best));
                        usedMovesThisFlight += goMoves;
                        current = best;
                    }



                    if (!ordered.isEmpty()) {
                        Position last = startPoint;

                        int entriesBeforeDay = droneEntries.size();
                        int movesBeforeDay = droneMoves;

                        for (Position tgt : ordered) {
                            String legK = helper.legKey(last, tgt);
                            List<Position> leg = pathStore.get(legK);
                            if (leg == null) continue;

                            List<Position> fp = new ArrayList<>(leg);

                            // Hover twice at tgt (delivery)
                            fp.add(new Position(tgt.getLng(), tgt.getLat()));
                            fp.add(new Position(tgt.getLng(), tgt.getLat()));

                            MedDispatchRecord rec = recordByPosKey.get(helper.coordKey(tgt));
                            if (rec == null) continue;

                            DeliveryEntry de = new DeliveryEntry();
                            de.setDeliveryId(rec.getId());
                            de.setFlightPath(fp);

                            droneEntries.add(de);
                            assignedDeliveries.add(rec.getId());
                            last = tgt;
                        }

                        // RETURN to service point: append to last delivery's flight path
                        List<Position> ret = pathStore.get(helper.legKey(last, startPoint));
                        if (ret != null && !droneEntries.isEmpty()) {
                            droneEntries.get(droneEntries.size() - 1)
                                    .getFlightPath()
                                    .addAll(ret);
                        }

                        // COUNT MOVES ONLY FOR TODAY'S NEW ENTRIES FOR THIS DRONE
                        for (int i = entriesBeforeDay; i < droneEntries.size(); i++) {
                            List<Position> fp = droneEntries.get(i).getFlightPath();
                            for (int j = 1; j < fp.size(); j++) {
                                Position a = fp.get(j - 1);
                                Position b = fp.get(j);
                                if (!a.getLng().equals(b.getLng()) || !a.getLat().equals(b.getLat())) {
                                    droneMoves++;
                                }
                            }
                        }

                        int movesThisDay = droneMoves - movesBeforeDay;

                        // COST FOR THIS DAY'S FLIGHT FOR THIS DRONE
                        double init = drone.getCapability().getCostInitial();
                        double perMove = drone.getCapability().getCostPerMove();
                        double fin = drone.getCapability().getCostFinal();

                        droneCost += init + (perMove * movesThisDay) + fin;
                    }
                }

                if (!droneEntries.isEmpty()) {
                    plan.setDeliveries(droneEntries);
                    allDronePlans.add(plan);

                    totalMoves += droneMoves;
                    totalCost += droneCost;
                }


                if (assignedDeliveries.size() == recordList.size()) {
                    break;
                }
            }

            resp.setDronePaths(allDronePlans);
            resp.setTotalCost(totalCost);
            resp.setTotalMoves(totalMoves);

            return resp;


        }




        @Override
        public Map<String, Object> calcDeliveryPathAsGeoJson (List < MedDispatchRecord > records) {


            CalcDeliveryPathsResponse resp = calcDeliveryPath(records);

            if (resp.getDronePaths() == null || resp.getDronePaths().isEmpty()) {
                return Map.of("error", "No valid drone path found.");
            }

            //only one drone is selected for this endpoint
            DeliveryPaths dp = resp.getDronePaths().get(0);

            List<Map<String, Object>> features = new ArrayList<>();


            List<List<Double>> lineCoords = new ArrayList<>();

            for (DeliveryEntry de : dp.getDeliveries()) {
                for (Position p : de.getFlightPath()) {
                    lineCoords.add(List.of(p.getLng(), p.getLat()));
                }
            }

            Map<String, Object> lineFeature = new LinkedHashMap<>();
            lineFeature.put("type", "Feature");

            Map<String, Object> lineProps = new LinkedHashMap<>();
            lineProps.put("droneId", dp.getDroneId());
            lineFeature.put("properties", lineProps);

            Map<String, Object> lineGeom = new LinkedHashMap<>();
            lineGeom.put("type", "LineString");
            lineGeom.put("coordinates", lineCoords);

            lineFeature.put("geometry", lineGeom);
            features.add(lineFeature);


            //     convert ALL No-Fly Zones into GeoJSON Polygons


            noFlyZoneHelper.refreshZones();
            for (NoFlyZone z : noFlyZoneHelper.zones) {
                System.out.println("No-fly zone with vertices: " + z.getVertices());
            }


            Region[] zones = noFlyZoneHelper.getNoFlyZones();

            for (Region zone : zones) {

                Map<String, Object> polyFeature = new LinkedHashMap<>();
                polyFeature.put("type", "Feature");

                Map<String, Object> props = new LinkedHashMap<>();
                props.put("zoneName", zone.getName());
                polyFeature.put("properties", props);

                Map<String, Object> geom = new LinkedHashMap<>();
                geom.put("type", "Polygon");

                // One polygon ring
                List<List<Double>> ring = new ArrayList<>();

                for (Position p : zone.getVertices()) {
                    ring.add(List.of(p.getLng(), p.getLat()));
                }

                // close polygon
                if (!ring.isEmpty() && !ring.get(0).equals(ring.get(ring.size() - 1))) {
                    ring.add(ring.get(0));
                }

                geom.put("coordinates", List.of(ring));
                polyFeature.put("geometry", geom);

                features.add(polyFeature);
            }

            // ============================================================
            //                      4) Final FeatureCollection
            // ============================================================

            Map<String, Object> fc = new LinkedHashMap<>();
            fc.put("type", "FeatureCollection");
            fc.put("features", features);

            return fc;
        }

}








