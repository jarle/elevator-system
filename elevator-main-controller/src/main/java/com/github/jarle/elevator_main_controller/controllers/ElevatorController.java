package com.github.jarle.elevator_main_controller.controllers;

import java.time.LocalDateTime;
import java.util.Optional;

import com.github.jarle.elevator_domain.Direction;
import com.github.jarle.elevator_main_controller.service.ElevatorControlService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElevatorController {
    final ElevatorControlService elevatorControlService;

    public ElevatorController(final ElevatorControlService elevatorControlService) {
        this.elevatorControlService = elevatorControlService;
    }

    @GetMapping("stopElevator")
    public void stopElevator() {
        this.elevatorControlService.stopElevator();
    }

    @GetMapping("resumeElevator")
    public void resumeElevator() {
        this.elevatorControlService.resumeElevator();
    }

    @GetMapping("currentDirection")
    public Direction currentDirection() {
        return this.elevatorControlService.currentDirection();
    }

    @GetMapping("remainingTravelTime")
    public Optional<Long> remainingTravelTime() {
        return this.elevatorControlService.currentTravelTime()
                .map(
                        travelTime -> travelTime.remaining(LocalDateTime.now())
                );
    }
}
