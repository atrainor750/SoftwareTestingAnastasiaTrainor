package uk.ac.ed.acp.cw2.controller;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ed.acp.cw2.dtos.*;
import uk.ac.ed.acp.cw2.service.RestService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestService regionService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public RestService regionService() {

            return Mockito.mock(RestService.class);
        }
    }

    @Test
    void testDistanceToEndpoint() throws Exception {
        when(regionService.distanceTo(any(LngLatPairRequest.class))).thenReturn(0.00123);

        String json = """
            {
              "position1": {"lat": 1.0, "lng": 1.0},
              "position2": {"lat": 1.1, "lng": 1.1}
            }
            """;

        mockMvc.perform(post("/api/v1/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("0.00123"));
    }
    @Test
    void testIsCloseToEndpointReturnsTrue() throws Exception {
        // Mock behavior
        when(regionService.isCloseTo(any(LngLatPairRequest.class))).thenReturn(true);

        LngLatPairRequest request = new LngLatPairRequest();
        request.setPosition1(new Position(55.0, -3.0));
        request.setPosition2(new Position(55.0001, -3.0001));

        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testNextPositionEndpointReturnsPosition() throws Exception {
        Position expected = new Position(-3.001, 55.0);

        NextMoveRequest req = new NextMoveRequest();
        req.setStart(new Position(-3.0, 55.0));
        req.setAngle(90.0);

        when(regionService.nextPosition(any(NextMoveRequest.class))).thenReturn(expected);

        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lat").value(55))
                .andExpect(jsonPath("$.lng").value(-3.001));
    }

    @Test
    void testIsInRegionEndpointReturnsFalse() throws Exception {
        when(regionService.isInRegion(any(InRegionRequest.class))).thenReturn(false);

        InRegionRequest req = new InRegionRequest();
        req.setPosition(new Position(55.0, -3.0));

        Region region = new Region();
        region.setName("TestRegion");
        region.setVertices(java.util.List.of(
                new Position(55.0, -3.0),
                new Position(55.0, -3.1),
                new Position(55.1, -3.1),
                new Position(55.1, -3.0),
                new Position(55.0, -3.0)
        ));
        req.setRegion(region);

        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}

