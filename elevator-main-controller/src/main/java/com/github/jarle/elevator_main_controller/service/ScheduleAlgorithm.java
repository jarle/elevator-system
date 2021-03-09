package com.github.jarle.elevator_main_controller.service;

import com.github.jarle.elevator_domain.ElevatorDestination;
import com.github.jarle.elevator_main_controller.domain.DestinationSchedule;

public class ScheduleAlgorithm {

    public static DestinationSchedule schedule(final ElevatorDestination destination, final DestinationSchedule currentSchedule) {
        // todo: create optimal route
        return currentSchedule.add(destination);
    }
}
