package com.github.jarle.elevator_entity.controller;

import java.util.Optional;

import com.github.jarle.elevator_domain.Direction;
import com.github.jarle.elevator_domain.TravelTime;
import com.github.jarle.elevator_entity.service.ElevatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElevatorEntityController {
    Logger logger = LoggerFactory.getLogger(ElevatorEntityController.class);

    final ElevatorService elevatorService;

    @Autowired
    public ElevatorEntityController(final ElevatorService elevatorService) {
        this.elevatorService = elevatorService;
    }

    @GetMapping("stop")
    public void stop() {
        this.elevatorService.stop();
    }

    @GetMapping("resume")
    public void resume() {
        this.elevatorService.resume();
    }

    @GetMapping("currentDirection")
    public Direction currentDirection() {
        return elevatorService.currentDirection();
    }

    @GetMapping("timeToDestination")
    public Optional<TravelTime> timeToDestination() {
        return elevatorService.timeToDestination();
    }
}
