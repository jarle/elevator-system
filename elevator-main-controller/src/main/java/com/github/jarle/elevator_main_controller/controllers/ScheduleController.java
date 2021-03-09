package com.github.jarle.elevator_main_controller.controllers;


import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Optional;

import com.github.jarle.elevator_domain.ElevatorDestination;
import com.github.jarle.elevator_main_controller.domain.DestinationSchedule;
import com.github.jarle.elevator_main_controller.service.ElevatorSchedulingService;
import com.github.jarle.elevator_main_controller.service.IllegalDestinationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ScheduleController {
    Logger logger = LoggerFactory.getLogger(ScheduleController.class);
    private final ElevatorSchedulingService elevatorSchedulingService;

    @Autowired
    public ScheduleController(final ElevatorSchedulingService elevatorSchedulingService) {
        this.elevatorSchedulingService = elevatorSchedulingService;
    }

    @PostMapping(path = "scheduleDestination")
    public ElevatorDestination scheduleDestination(@RequestBody ElevatorDestination destination) {
        try {
            this.elevatorSchedulingService.scheduleDestination(destination);
        } catch (IllegalDestinationException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage(), e);
        }
        return destination;
    }

    @PostMapping(path = "unscheduleDestination")
    public ElevatorDestination unscheduleDestination(@RequestBody ElevatorDestination destination) {
        try {
            this.elevatorSchedulingService.unscheduleDestination(destination);
        } catch (IllegalDestinationException e) {
            throw new ResponseStatusException(NOT_FOUND, e.getMessage(), e);
        }
        return destination;
    }

    @GetMapping(path = "nextDestination")
    public Optional<ElevatorDestination> nextDestination() {
        return this.elevatorSchedulingService.currentSchedule().next();
    }

    @GetMapping(path = "currentSchedule")
    public DestinationSchedule currentSchedule() {
        return this.elevatorSchedulingService.currentSchedule();
    }

    // for testing
    @GetMapping(path = "clearSchedule")
    public void clearSchedule() {
        this.elevatorSchedulingService.clearSchedule();
    }

    @GetMapping(path = "populateSchedule")
    public void populateSchedule() {
        this.elevatorSchedulingService.populateSchedule();
    }
}
