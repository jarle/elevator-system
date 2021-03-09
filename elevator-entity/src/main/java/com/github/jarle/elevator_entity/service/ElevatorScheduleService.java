package com.github.jarle.elevator_entity.service;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.time.Duration;
import java.util.Optional;

import com.github.jarle.elevator_domain.ElevatorDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
public class ElevatorScheduleService {
    Logger logger = LoggerFactory.getLogger(ElevatorScheduleService.class);

    private final RestTemplate mainController;

    public ElevatorScheduleService(final RestTemplateBuilder restTemplateBuilder) {
        this.mainController = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:8080"))
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    public Optional<ElevatorDestination> getNextDestination() {
        Optional<ElevatorDestination> result;
        try {
            result = ofNullable(mainController.getForObject("/nextDestination", ElevatorDestination.class));
        } catch (RestClientException ex) {
            logger.error(ex.getMessage());
            return empty();
        }
        result.ifPresent(destination -> logger.info("Next destination is {}", destination));
        return result;
    }

    public void unScheduleDestination(ElevatorDestination destination) {
        logger.info("Unscheduling {}", destination);
        try {
            mainController.postForObject("/unscheduleDestination", destination, ElevatorDestination.class);
        } catch (RestClientException ex) {
            ex.printStackTrace();
            return;
        }
        logger.info("Unscheduled {}", destination);
    }
}
