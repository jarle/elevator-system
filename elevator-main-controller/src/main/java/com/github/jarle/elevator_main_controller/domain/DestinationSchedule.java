package com.github.jarle.elevator_main_controller.domain;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.jarle.elevator_domain.ElevatorDestination;


public class DestinationSchedule {
    private List<ElevatorDestination> schedule;

    public DestinationSchedule(final List<ElevatorDestination> schedule) {
        this.schedule = schedule;
    }

    public DestinationSchedule add(final ElevatorDestination destination) {
        this.schedule.add(destination);
        return this;
    }

    public DestinationSchedule without(final ElevatorDestination destination) {
        this.schedule = this.schedule
                .stream()
                .filter(not(destination::equals))
                .collect(toList());
        return this;
    }


    public List<ElevatorDestination> getSchedule() {
        return schedule;
    }

    @Override
    public String toString() {
        return "DestinationSchedule{" +
               "schedule=" + schedule +
               '}';
    }

    public static DestinationSchedule empty() {
        return new DestinationSchedule(new ArrayList<>());
    }

    public Optional<ElevatorDestination> next() {
        if (this.schedule.isEmpty()) {
            return Optional.empty();
        }
        else {
            return Optional.of(this.schedule.get(0));
        }
    }
}
