package uk.ac.ed.acp.cw2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.ac.ed.acp.cw2.dtos.Capability;
import uk.ac.ed.acp.cw2.dtos.Drone;
import uk.ac.ed.acp.cw2.dtos.Query;
import uk.ac.ed.acp.cw2.Helpers.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DroneServiceImplTest {

    private DroneServiceImpl droneService;

    // Constructor dependencies
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

        // fixed drone dataset for repeatable tests
        when(droneHelper.getAllDrones()).thenReturn(sampleDrones());
    }

    // -------------------------
    // R1: droneDetails
    // -------------------------

    @Test
    void droneDetails_returnsMatchingDrone() {
        Drone d = droneService.droneDetails("4");

        assertNotNull(d);
        assertEquals("4", d.getId());
        assertEquals("Drone 4", d.getName());
        assertEquals(8.0, d.getCapability().getCapacity());
        assertTrue(d.getCapability().isHeating());
    }

    @Test
    void droneDetails_returnsNullIfNotFound() {
        assertNull(droneService.droneDetails("999"));
    }

    // -------------------------
    // R1: queryAsPath (exact match on attribute)
    // -------------------------

    @Test
    void queryAsPath_filtersByCoolingTrue() {
        List<String> ids = droneService.queryAsPath("cooling", "true");

        // From your data: drones 1,5,8,9 have cooling=true
        assertTrue(ids.containsAll(List.of("1", "5", "8", "9")));
        assertEquals(4, ids.size());
    }

    @Test
    void queryAsPath_filtersByCapacity8() {
        List<String> ids = droneService.queryAsPath("capacity", "8.0");

        // From your data: drones 2,4,7,9 have capacity 8.0
        assertTrue(ids.containsAll(List.of("2", "4", "7", "9")));
        assertEquals(4, ids.size());
    }

    @Test
    void queryAsPath_unknownAttribute_returnsEmpty() {
        List<String> ids = droneService.queryAsPath("unknown", "anything");
        assertTrue(ids.isEmpty());
    }

    // -------------------------
    // R1: query(List<Query>) (operators =, !=, <, >)
    // -------------------------

    @Test
    void query_singleCondition_capacityGreaterThan8() {
        Query q = new Query();
        q.setAttribute("capacity");
        q.setOperator(">");
        q.setValue("8.0");

        List<String> ids = droneService.query(List.of(q));

        // capacity > 8.0 : drones 3(20),5(12),8(20),10(12)
        assertTrue(ids.containsAll(List.of("3", "5", "8", "10")));
        assertEquals(4, ids.size());
    }

    @Test
    void query_multipleConditions_andSemantics() {
        // cooling = true AND heating = true
        Query q1 = new Query();
        q1.setAttribute("cooling");
        q1.setOperator("=");
        q1.setValue("true");

        Query q2 = new Query();
        q2.setAttribute("heating");
        q2.setOperator("=");
        q2.setValue("true");

        List<String> ids = droneService.query(List.of(q1, q2));

        // cooling=true and heating=true: drones 1,5,9
        assertTrue(ids.containsAll(List.of("1", "5", "9")));
        assertEquals(3, ids.size());
    }

    @Test
    void query_notEqualsBoolean() {
        // heating != true  => heating is false
        Query q = new Query();
        q.setAttribute("heating");
        q.setOperator("!=");
        q.setValue("true");

        List<String> ids = droneService.query(List.of(q));

        // heating=false: drones 3,8,10
        assertTrue(ids.containsAll(List.of("3", "8", "10")));
        assertEquals(3, ids.size());
    }

    // -------------------------
    // Test data
    // -------------------------

    private static List<Drone> sampleDrones() {
        List<Drone> drones = new ArrayList<>();

        drones.add(drone("Drone 1", "1", true,  true,  4.0, 2000, 0.01, 4.3, 6.5));
        drones.add(drone("Drone 2", "2", false, true,  8.0, 1000, 0.03, 2.6, 5.4));
        drones.add(drone("Drone 3", "3", false, false, 20.0, 4000, 0.05, 9.5, 11.5));
        drones.add(drone("Drone 4", "4", false, true,  8.0, 1000, 0.02, 1.4, 2.5));
        drones.add(drone("Drone 5", "5", true,  true,  12.0,1500, 0.04, 1.8, 3.5));
        drones.add(drone("Drone 6", "6", false, true,  4.0, 2000, 0.03, 3.0, 4.0));
        drones.add(drone("Drone 7", "7", false, true,  8.0, 1000, 0.015,1.4, 2.2));
        drones.add(drone("Drone 8", "8", true,  false, 20.0,4000, 0.04, 5.4, 12.5));
        drones.add(drone("Drone 9", "9", true,  true,  8.0, 1000, 0.06, 2.4, 1.5));
        drones.add(drone("Drone 10","10",false, false, 12.0,1500, 0.07, 1.4, 3.5));

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


