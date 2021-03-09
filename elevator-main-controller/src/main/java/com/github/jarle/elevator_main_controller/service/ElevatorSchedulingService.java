package com.github.jarle.elevator_main_controller.service;

import static com.github.jarle.elevator_main_controller.domain.DestinationSchedule.empty;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.github.jarle.elevator_domain.ElevatorDestination;
import com.github.jarle.elevator_domain.ElevatorDestination.ElevatorDestinationBuilder;
import com.github.jarle.elevator_main_controller.domain.DestinationSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElevatorSchedulingService {
    Logger logger = LoggerFactory.getLogger(ElevatorSchedulingService.class);

    private final List<ElevatorDestination> validDestinations;
    private DestinationSchedule currentSchedule;

    @Autowired
    public ElevatorSchedulingService() {
        this.validDestinations = LongStream.range(0, 128)
                .boxed()
                .map(ElevatorDestination.builder()::destinationId)
                .map(ElevatorDestinationBuilder::build)
                .collect(toList());

        this.currentSchedule = empty();
    }

    public void scheduleDestination(final ElevatorDestination destination) throws IllegalDestinationException {
        validateDestination(destination);

        logger.info("Scheduling {}", destination);
        logger.info("Current schedule: {}", this.currentSchedule);

        this.currentSchedule = ScheduleAlgorithm.schedule(destination, this.currentSchedule);

        logger.info("Scheduled {}", destination);
        logger.info("Current schedule: {}", this.currentSchedule);
    }

    public void unscheduleDestination(final ElevatorDestination destination) throws IllegalDestinationException {
        validateDestination(destination);

        logger.info("Unscheduling {}", destination);
        logger.info("Current schedule: {}", this.currentSchedule);

        this.currentSchedule = this.currentSchedule.without(destination);

        logger.info("Uncheduled {}", destination);
        logger.info("Current schedule: {}", this.currentSchedule);
    }

    public DestinationSchedule currentSchedule() {
        return this.currentSchedule;
    }

    public void clearSchedule() {
        this.currentSchedule = empty();
    }

    private void validateDestination(final ElevatorDestination destination) throws IllegalDestinationException {
        if (!this.validDestinations.contains(destination)) {
            throw new IllegalDestinationException(destination, validDestinations);
        }
    }

    public void populateSchedule() {
        final DestinationSchedule newSchedule = empty();

        Stream.of(3L, 1L, 2L, 1L, 42L, 1L, 127L, 1L)
                .map(ElevatorDestination.builder()::destinationId)
                .map(ElevatorDestinationBuilder::build)
                .forEach(newSchedule::add);

        this.currentSchedule = newSchedule;
    }
}
