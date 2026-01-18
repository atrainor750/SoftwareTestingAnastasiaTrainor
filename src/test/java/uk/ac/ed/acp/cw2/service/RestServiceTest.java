package uk.ac.ed.acp.cw2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import uk.ac.ed.acp.cw2.dtos.Query;          // IMPORTANT: your DTO Query
import uk.ac.ed.acp.cw2.dtos.Drone;
import uk.ac.ed.acp.cw2.dtos.Capability;
import uk.ac.ed.acp.cw2.dtos.Position;
import uk.ac.ed.acp.cw2.dtos.LngLatPairRequest;
import uk.ac.ed.acp.cw2.dtos.NextMoveRequest;
import uk.ac.ed.acp.cw2.dtos.InRegionRequest;
import uk.ac.ed.acp.cw2.dtos.Region;
import uk.ac.ed.acp.cw2.Helpers.*;
import uk.ac.ed.acp.cw2.*;
import uk.ac.ed.acp.cw2.service.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RestServiceTest {

    private RestService restService;
    private DroneHelper droneHelper; // adjust type name if yours differs

    @BeforeEach
    void setUp() throws Exception {
        restService = new RestService();
        droneHelper = Mockito.mock(DroneHelper.class);



        when(droneHelper.getAllDrones()).thenReturn(sampleDrones());
    }


    // -------------------------
    // R1/R2: distanceTo + isCloseTo
    // -------------------------

    @Test
    void distanceTo_validPositions_returnsEuclideanDistance() {
        LngLatPairRequest req = new LngLatPairRequest();
        req.setPosition1(new Position(55.0, -3.0));
        req.setPosition2(new Position(55.0, -3.00015));

        double d = restService.distanceTo(req);
        // lat diff 0, lng diff 0.00015 => distance 0.00015
        assertEquals(0.00015, d, 1e-12);
    }

    @Test
    void distanceTo_invalidPosition_throwsBadRequest() {
        LngLatPairRequest req = new LngLatPairRequest();
        req.setPosition1(new Position(200.0, -3.0));   // invalid latitude
        req.setPosition2(new Position(55.0, -3.0));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> restService.distanceTo(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void isCloseTo_trueWhenUnderThreshold() {
        // threshold is CLOSE_INT = 0.00015, and method uses strict "<"
        LngLatPairRequest req = new LngLatPairRequest();
        req.setPosition1(new Position(55.0, -3.0));
        req.setPosition2(new Position(55.0, -3.000149)); // slightly less than threshold

        assertTrue(restService.isCloseTo(req));
    }

    @Test
    void isCloseTo_falseWhenEqualToThreshold() {
        LngLatPairRequest req = new LngLatPairRequest();
        req.setPosition1(new Position(55.0, -3.0));
        req.setPosition2(new Position(55.0, -3.00015)); // exactly threshold

        assertFalse(restService.isCloseTo(req)); // strict <
    }

    @Test
    void isCloseTo_invalid_throwsBadRequest() {
        LngLatPairRequest req = new LngLatPairRequest();
        req.setPosition1(new Position(200.0, -3.0));
        req.setPosition2(new Position(55.0, -3.0));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> restService.isCloseTo(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    // -------------------------
    // R1/R2: nextPosition
    // -------------------------


    @Test
    void nextPosition_rejectsAngleNotMultipleOf22_5() {
        NextMoveRequest req = new NextMoveRequest();
        req.setStart(new Position(55.0, -3.0));
        req.setAngle(10.0);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> restService.nextPosition(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void nextPosition_rejectsAngleGreaterThan360() {
        NextMoveRequest req = new NextMoveRequest();
        req.setStart(new Position(55.0, -3.0));
        req.setAngle(361.0);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> restService.nextPosition(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void nextPosition_rejectsInvalidStartCoordinate() {
        NextMoveRequest req = new NextMoveRequest();
        req.setStart(new Position(200.0, -3.0));
        req.setAngle(0.0);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> restService.nextPosition(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    // -------------------------
    // R3: isInRegion (geometry + robustness checks)
    // -------------------------

    @Test
    void isInRegion_returnsTrueForInsidePoint() {
        InRegionRequest req = new InRegionRequest();
        req.setPosition(new Position(55.05, -3.05));
        req.setRegion(squareRegion());

        assertTrue(restService.isInRegion(req));
    }

    @Test
    void isInRegion_returnsFalseForOutsidePoint() {
        InRegionRequest req = new InRegionRequest();
        req.setPosition(new Position(55.2, -3.2));
        req.setRegion(squareRegion());

        assertFalse(restService.isInRegion(req));
    }

    @Test
    void isInRegion_returnsTrueWhenOnEdge() {
        InRegionRequest req = new InRegionRequest();
        // point on left edge of square
        req.setPosition(new Position(55.05, -3.1));
        req.setRegion(squareRegion());

        assertTrue(restService.isInRegion(req)); // your code treats edge as true
    }

    @Test
    void isInRegion_rejectsRegionWithTooFewVertices() {
        InRegionRequest req = new InRegionRequest();
        req.setPosition(new Position(55.0, -3.0));

        Region region = new Region();
        region.setName("BadRegion");
        region.setVertices(List.of(
                new Position(55.0, -3.0),
                new Position(55.1, -3.0),
                new Position(55.0, -3.0) // only 3 points (and closed), but size < 4 triggers your check
        ));
        req.setRegion(region);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> restService.isInRegion(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void isInRegion_rejectsRegionNotClosed() {
        InRegionRequest req = new InRegionRequest();
        req.setPosition(new Position(55.05, -3.05));

        Region region = new Region();
        region.setName("NotClosed");
        region.setVertices(List.of(
                new Position(55.0, -3.0),
                new Position(55.0, -3.1),
                new Position(55.1, -3.1),
                new Position(55.1, -3.0)
                // missing closing vertex equal to first
        ));
        req.setRegion(region);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> restService.isInRegion(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void isInRegion_rejectsRegionWithInvalidVertexCoordinate() {
        InRegionRequest req = new InRegionRequest();
        req.setPosition(new Position(55.05, -3.05));

        Region region = new Region();
        region.setName("InvalidVertex");
        region.setVertices(List.of(
                new Position(55.0, -3.0),
                new Position(200.0, -3.1), // invalid lat
                new Position(55.1, -3.1),
                new Position(55.0, -3.0)  // closed
        ));
        req.setRegion(region);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> restService.isInRegion(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    // -------------------------
    // Helpers
    // -------------------------

    private static Region squareRegion() {
        // square around lat 55.0..55.1 and lng -3.1..-3.0, closed
        Region region = new Region();
        region.setName("Square");
        region.setVertices(List.of(
                new Position(55.0, -3.0),
                new Position(55.0, -3.1),
                new Position(55.1, -3.1),
                new Position(55.1, -3.0),
                new Position(55.0, -3.0)
        ));
        return region;
    }

    private static List<Drone> sampleDrones() {
        List<Drone> drones = new ArrayList<>();

        drones.add(drone("Drone 1", "1", true, true, 4.0, 2000, 0.01, 4.3, 6.5));
        drones.add(drone("Drone 2", "2", false, true, 8.0, 1000, 0.03, 2.6, 5.4));
        drones.add(drone("Drone 3", "3", false, false, 20.0, 4000, 0.05, 9.5, 11.5));
        drones.add(drone("Drone 4", "4", false, true, 8.0, 1000, 0.02, 1.4, 2.5));
        drones.add(drone("Drone 5", "5", true, true, 12.0, 1500, 0.04, 1.8, 3.5));
        drones.add(drone("Drone 6", "6", false, true, 4.0, 2000, 0.03, 3.0, 4.0));
        drones.add(drone("Drone 7", "7", false, true, 8.0, 1000, 0.015, 1.4, 2.2));
        drones.add(drone("Drone 8", "8", true, false, 20.0, 4000, 0.04, 5.4, 12.5));
        drones.add(drone("Drone 9", "9", true, true, 8.0, 1000, 0.06, 2.4, 1.5));
        drones.add(drone("Drone 10", "10", false, false, 12.0, 1500, 0.07, 1.4, 3.5));

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

