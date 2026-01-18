package uk.ac.ed.acp.cw2.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import uk.ac.ed.acp.cw2.Helpers.DroneHelper;
import uk.ac.ed.acp.cw2.Helpers.*;
import uk.ac.ed.acp.cw2.dtos.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


class R1Tests {

    private DroneServiceImpl droneService;

    private DroneHelper droneHelper;
    private Helper helper;
    private NoFlyZoneHelper noFlyZoneHelper;
    private Helper availabilityHelper;

    @BeforeEach
    void setUp() {
        droneHelper = Mockito.mock(DroneHelper.class);
        helper = Mockito.mock(Helper.class);
        noFlyZoneHelper = Mockito.mock(NoFlyZoneHelper.class);
        availabilityHelper = Mockito.mock(Helper.class);

        droneService = new DroneServiceImpl(droneHelper, helper, noFlyZoneHelper, availabilityHelper);

        when(droneHelper.getAllDrones()).thenReturn(sampleDrones());
    }


    // R1: getAllDrones


    @Test
    void getAllDrones_returnsFixtureList() {
        List<Drone> drones = droneService.getAllDrones();
        assertNotNull(drones);
        assertEquals(10, drones.size());
        assertEquals("1", drones.get(0).getId());
        assertEquals("Drone 10", drones.get(9).getName());
    }


    // R1: dronesWithCooling


    @Test
    void dronesWithCooling_true_returnsAllCoolingDrones() {
        // From fixture: drones 1,5,8,9 have cooling=true
        List<String> ids = droneService.dronesWithCooling(true);

        assertEquals(List.of("1", "5", "8", "9"), ids);
    }

    @Test
    void dronesWithCooling_false_returnsAllNonCoolingDrones() {
        // From fixture: 2,3,4,6,7,10 have cooling=false
        List<String> ids = droneService.dronesWithCooling(false);

        assertEquals(List.of("2", "3", "4", "6", "7", "10"), ids);
    }


    // R1: droneDetails


    @Test
    void droneDetails_returnsMatchingDrone() {
        Drone d = droneService.droneDetails("4");
        assertNotNull(d);
        assertEquals("4", d.getId());
        assertEquals("Drone 4", d.getName());
        assertEquals(8.0, d.getCapability().getCapacity(), 1e-12);
        assertTrue(d.getCapability().isHeating());
    }

    @Test
    void droneDetails_returnsNullIfNotFound() {
        assertNull(droneService.droneDetails("999"));
    }


    // R1: queryAsPath (exact match on attribute)


    @Test
    void queryAsPath_filtersById_exactMatch() {
        List<String> ids = droneService.queryAsPath("id", "3");
        assertEquals(List.of("3"), ids);
    }

    @Test
    void queryAsPath_filtersByCoolingTrue() {
        List<String> ids = droneService.queryAsPath("cooling", "true");
        assertEquals(List.of("1", "5", "8", "9"), ids);
    }

    @Test
    void queryAsPath_filtersByHeatingFalse() {
        // heating=false: drones 3,8,10
        List<String> ids = droneService.queryAsPath("heating", "false");
        assertEquals(List.of("3", "8", "10"), ids);
    }

    @Test
    void queryAsPath_filtersByCapacityExactStringMatch() {
        // capacity "8.0": drones 2,4,7,9
        List<String> ids = droneService.queryAsPath("capacity", "8.0");
        assertEquals(List.of("2", "4", "7", "9"), ids);
    }

    @Test
    void queryAsPath_filtersByMaxMovesExactStringMatch() {
        // maxMoves "1000": drones 2,4,7,9
        List<String> ids = droneService.queryAsPath("maxMoves", "1000");
        assertEquals(List.of("2", "4", "7", "9"), ids);
    }

    @Test
    void queryAsPath_filtersByNameExactMatch() {
        List<String> ids = droneService.queryAsPath("name", "Drone 6");
        assertEquals(List.of("6"), ids);
    }

    @Test
    void queryAsPath_unknownAttribute_returnsEmptyList() {
        List<String> ids = droneService.queryAsPath("unknown", "whatever");
        assertTrue(ids.isEmpty());
    }


    // R1: query(List<Query>) - operators and AND behaviour


    @Test
    void query_singleBooleanEquals_filtersCoolingTrue() {
        List<Query> q = List.of(query("cooling", "=", "true"));
        List<String> ids = droneService.query(q);

        assertEquals(List.of("1", "5", "8", "9"), ids);
    }

    @Test
    void query_singleBooleanNotEquals_filtersHeatingNotTrue() {
        // heating != true => heating=false => drones 3,8,10
        List<Query> q = List.of(query("heating", "!=", "true"));
        List<String> ids = droneService.query(q);

        assertEquals(List.of("3", "8", "10"), ids);
    }

    @Test
    void query_capacityGreaterThan_filtersCorrectly() {
        // capacity > 10.0 => 12.0 and 20.0
        // drones: 3 (20), 5 (12), 8 (20), 10 (12)
        List<Query> q = List.of(query("capacity", ">", "10.0"));
        List<String> ids = droneService.query(q);

        assertEquals(List.of("3", "5", "8", "10"), ids);
    }

    @Test
    void query_capacityLessThan_filtersCorrectly() {
        // capacity < 8.0 => capacity 4.0 only
        // drones: 1 (4), 6 (4)
        List<Query> q = List.of(query("capacity", "<", "8.0"));
        List<String> ids = droneService.query(q);

        assertEquals(List.of("1", "6"), ids);
    }

    @Test
    void query_maxMovesEquals_filtersCorrectly() {
        // maxMoves = 4000 => drones 3,8
        List<Query> q = List.of(query("maxMoves", "=", "4000"));
        List<String> ids = droneService.query(q);

        assertEquals(List.of("3", "8"), ids);
    }

    @Test
    void query_costPerMoveNotEquals_filtersCorrectly() {
        // costPerMove != 0.03
        // drones with 0.03 are 2 and 6; so all except 2,6
        List<Query> q = List.of(query("costPerMove", "!=", "0.03"));
        List<String> ids = droneService.query(q);

        assertEquals(List.of("1", "3", "4", "5", "7", "8", "9", "10"), ids);
    }

    @Test
    void query_combinedAnd_filtersByCoolingTrueAndHeatingTrue() {
        // cooling=true AND heating=true => drones 1,5,9
        List<Query> q = List.of(
                query("cooling", "=", "true"),
                query("heating", "=", "true")
        );
        List<String> ids = droneService.query(q);

        assertEquals(List.of("1", "5", "9"), ids);
    }

    @Test
    void query_combinedAnd_numericAndBoolean_filtersCorrectly() {
        // cooling=true AND capacity=8.0 => only drone 9 (cooling true, capacity 8.0)
        List<Query> q = List.of(
                query("cooling", "=", "true"),
                query("capacity", "=", "8.0")
        );
        List<String> ids = droneService.query(q);

        assertEquals(List.of("9"), ids);
    }

    @Test
    void query_emptyQueryList_returnsAllDrones() {
        // Code: flag1 starts true; if queryList empty, flag1 remains true => all drones included.
        List<String> ids = droneService.query(List.of());
        assertEquals(List.of("1","2","3","4","5","6","7","8","9","10"), ids);
    }



    private static Query query(String attribute, String operator, String value) {
        Query q = new Query();
        q.setAttribute(attribute);
        q.setOperator(operator);
        q.setValue(value);
        return q;
    }

    private static List<Drone> sampleDrones() {
        List<Drone> drones = new ArrayList<>();

        drones.add(drone("Drone 1", "1", true,  true,  4.0,  2000, 0.01, 4.3, 6.5));
        drones.add(drone("Drone 2", "2", false, true,  8.0,  1000, 0.03, 2.6, 5.4));
        drones.add(drone("Drone 3", "3", false, false, 20.0, 4000, 0.05, 9.5, 11.5));
        drones.add(drone("Drone 4", "4", false, true,  8.0,  1000, 0.02, 1.4, 2.5));
        drones.add(drone("Drone 5", "5", true,  true,  12.0, 1500, 0.04, 1.8, 3.5));
        drones.add(drone("Drone 6", "6", false, true,  4.0,  2000, 0.03, 3.0, 4.0));
        drones.add(drone("Drone 7", "7", false, true,  8.0,  1000, 0.015, 1.4, 2.2));
        drones.add(drone("Drone 8", "8", true,  false, 20.0, 4000, 0.04, 5.4, 12.5));
        drones.add(drone("Drone 9", "9", true,  true,  8.0,  1000, 0.06, 2.4, 1.5));
        drones.add(drone("Drone 10","10",false, false, 12.0, 1500, 0.07, 1.4, 3.5));

        return drones;
    }

    private static Drone drone(String name, String id,
                               boolean cooling, boolean heating,
                               double capacity, int maxMoves,
                               double costPerMove, double costInitial, double costFinal) {

        Drone d = new Drone();
        d.setName(name);
        d.setId(id);

        Capability c = new Capability();
        c.setCooling(cooling);
        c.setHeating(heating);
        c.setCapacity(capacity);
        c.setMaxMoves(maxMoves);
        c.setCostPerMove(costPerMove);
        c.setCostInitial(costInitial);
        c.setCostFinal(costFinal);

        d.setCapability(c);
        return d;
    }
}
