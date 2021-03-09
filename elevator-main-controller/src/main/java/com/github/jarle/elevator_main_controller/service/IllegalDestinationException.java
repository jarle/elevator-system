package com.github.jarle.elevator_main_controller.service;

import java.util.List;

import com.github.jarle.elevator_domain.ElevatorDestination;


public class IllegalDestinationException extends Exception {
    private final ElevatorDestination destination;
    private final List<ElevatorDestination> validDestinations;

    public IllegalDestinationException(final ElevatorDestination destination, final List<ElevatorDestination> validDestinations) {
        this.destination = destination;
        this.validDestinations = validDestinations;
    }

    @Override
    public String getMessage() {
        return String.format("Not a valid destination: %s. Valid destinations are: %s", destination, validDestinations);
    }
}
