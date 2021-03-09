package com.github.jarle.elevator_entity.service;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.Optional;

import com.github.jarle.elevator_domain.Direction;
import com.github.jarle.elevator_entity.domain.Elevator;
import com.github.jarle.elevator_domain.TravelTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElevatorService {
    Logger logger = LoggerFactory.getLogger(ElevatorService.class);

    private final Elevator elevator;

    @Autowired
    public ElevatorService(final Elevator elevator) {
        this.elevator = elevator;
        newSingleThreadExecutor().submit(this.elevator);
    }

    public void stop() {
        logger.info("Stopping elevator");
        this.elevator.stop();
    }

    public void resume() {
        logger.info("Resuming elevator");
        this.elevator.resume();
    }

    public Optional<TravelTime> timeToDestination() {
        return elevator.remainingTimeToDestination();
    }

    public Direction currentDirection() {
        return elevator.currentDirection();
    }
}
