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
import org.springframework.test.web.servlet.ResultActions;

import uk.ac.ed.acp.cw2.service.DroneService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    @WebMvcTest(controllers = CW2PostController.class)
    @AutoConfigureMockMvc
    @Import(R2NegativeInputMvcTest.ControllerTestConfig.class)
    class R2NegativeInputMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @TestConfiguration
        static class ControllerTestConfig {
            @Bean
            DroneService droneService() {
                // service behaviour doesn't matter for validation/parsing failures
                return Mockito.mock(DroneService.class);
            }
        }


        private void assertBadRequest(ResultActions ra) throws Exception {
            ra.andExpect(status().isBadRequest());
        }

        // R2 corpus: malformed JSON

        @Test
        void query_malformedJson_returns400() throws Exception {
            String brokenJson = """
            [
              {"attribute":"cooling","operator":"=","value":"true"
            ]
            """; // missing closing }

            assertBadRequest(
                    mockMvc.perform(post("/api/v1/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(brokenJson))
            );
        }


        // R2 corpus: missing required fields (@NotNull)

        @Test
        void query_missingAttribute_returns400() throws Exception {
            String json = """
            [
              {"operator":"=","value":"true"}
            ]
            """;

            assertBadRequest(
                    mockMvc.perform(post("/api/v1/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
            );
        }

        @Test
        void query_missingOperator_returns400() throws Exception {
            String json = """
            [
              {"attribute":"cooling","value":"true"}
            ]
            """;

            assertBadRequest(
                    mockMvc.perform(post("/api/v1/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
            );
        }

        @Test
        void query_missingValue_returns400() throws Exception {
            String json = """
            [
              {"attribute":"cooling","operator":"="}
            ]
            """;

            assertBadRequest(
                    mockMvc.perform(post("/api/v1/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
            );
        }


        //  wrong top-level type (object not array)
        @Test
        void query_notAnArray_returns400() throws Exception {
            String jsonObject = """
            {"attribute":"cooling","operator":"=","value":"true"}
            """;

            assertBadRequest(
                    mockMvc.perform(post("/api/v1/query")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonObject))
            );
        }
    }


