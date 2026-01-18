package uk.ac.ed.acp.cw2.controller;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.ac.ed.acp.cw2.service.DroneService;

@TestConfiguration
public class ControllerTestConfig {

    @Bean
    public DroneService droneService() {
        return Mockito.mock(DroneService.class);
    }
}
