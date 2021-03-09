package com.github.jarle.elevator_main_controller.service;

import java.time.Duration;
import java.util.Optional;

import com.github.jarle.elevator_domain.Direction;
import com.github.jarle.elevator_domain.TravelTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
public class ElevatorControlService {
    Logger logger = LoggerFactory.getLogger(ElevatorControlService.class);

    private final RestTemplate elevatorEntityController;

    public ElevatorControlService(final RestTemplateBuilder restTemplateBuilder) {
        this.elevatorEntityController = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://127.0.0.1:8081"))
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void stopElevator() {
        logger.info("Stopping elevator");
        elevatorEntityController.getForObject("/stop", Void.class);
        logger.info("Stopped elevator");
    }

    public void resumeElevator() {
        logger.info("Resuming elevator");
        elevatorEntityController.getForObject("/resume", Void.class);
        logger.info("Resumed elevator");
    }

    public Direction currentDirection() {
        return elevatorEntityController.getForObject("/currentDirection", Direction.class);
    }

    public Optional<TravelTime> currentTravelTime() {
        return Optional.ofNullable(elevatorEntityController.getForObject("/timeToDestination", TravelTime.class));
    }
}
