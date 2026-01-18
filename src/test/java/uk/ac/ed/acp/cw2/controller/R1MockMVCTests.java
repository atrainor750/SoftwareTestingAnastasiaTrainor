package uk.ac.ed.acp.cw2.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.ed.acp.cw2.service.DroneService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CW2PostController.class)
@AutoConfigureMockMvc
@Import(R1MockMVCTests.ControllerTestConfig.class)
class R1MockMVCTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DroneService droneService;

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        DroneService droneService() {
            return Mockito.mock(DroneService.class);
        }
    }


    @Test
    void query_validBody_returns200AndServiceResult() throws Exception {
        when(droneService.query(anyList())).thenReturn(List.of("1", "5", "9"));

        String json = """
            [
              {"attribute":"cooling","operator":"=","value":"true"},
              {"attribute":"heating","operator":"=","value":"true"}
            ]
            """;

        mockMvc.perform(post("/api/v1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("1"))
                .andExpect(jsonPath("$[1]").value("5"))
                .andExpect(jsonPath("$[2]").value("9"));
    }

    @Test
    void query_missingAttribute_returns400() throws Exception {
        String json = """
            [
              {"operator":"=","value":"true"}
            ]
            """;

        mockMvc.perform(post("/api/v1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void query_missingOperator_returns400() throws Exception {
        String json = """
            [
              {"attribute":"cooling","value":"true"}
            ]
            """;

        mockMvc.perform(post("/api/v1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void query_missingValue_returns400() throws Exception {
        String json = """
            [
              {"attribute":"cooling","operator":"="}
            ]
            """;

        mockMvc.perform(post("/api/v1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void query_nullFields_returns400() throws Exception {
        String json = """
            [
              {"attribute":null,"operator":"=","value":"true"}
            ]
            """;

        mockMvc.perform(post("/api/v1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void query_malformedJson_returns400() throws Exception {
        String brokenJson = """
            [
              {"attribute":"cooling","operator":"=","value":"true"
            ]
            """;

        mockMvc.perform(post("/api/v1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(brokenJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void query_notAJsonArray_returns400() throws Exception {

        String jsonObject = """
            {"attribute":"cooling","operator":"=","value":"true"}
            """;

        mockMvc.perform(post("/api/v1/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject))
                .andExpect(status().isBadRequest());
    }
}




