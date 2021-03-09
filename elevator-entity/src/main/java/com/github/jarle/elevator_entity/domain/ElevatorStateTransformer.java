package com.github.jarle.elevator_entity.domain;

import static com.github.jarle.elevator_domain.Direction.DOWN;
import static com.github.jarle.elevator_domain.Direction.UP;
import static com.github.jarle.elevator_domain.TravelTime.TIME_PER_FLOOR_IN_MILLIS;
import static com.github.jarle.elevator_entity.domain.MotionState.INACTIVE;
import static com.github.jarle.elevator_entity.domain.MotionState.IN_MOTION;
import static com.github.jarle.elevator_entity.domain.MotionState.STOPPED;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Optional.empty;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import com.github.jarle.elevator_domain.Direction;
import com.github.jarle.elevator_domain.ElevatorDestination;
import com.github.jarle.elevator_domain.TravelTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns the updated {@link ElevatorState} for different state transitions.
 */
public class ElevatorStateTransformer {
    static Logger logger = LoggerFactory.getLogger(ElevatorStateTransformer.class);
    private static final ElevatorDestination INITIAL_DESTINATION = ElevatorDestination.builder().destinationId(1L).build();

    static ElevatorState initialState() {
        return new ElevatorState(
                INACTIVE,
                INITIAL_DESTINATION,
                empty(),
                empty(),
                empty()
        );
    }

    static ElevatorState arrivedAtDestinationState(final ElevatorState currentState) {
        final ElevatorDestination destination = currentState.getNextDestination().orElseThrow(
                () -> new IllegalStateException(String.format("Cannot arrive at destination when next destination is not set: %s", currentState))
        );
        logger.info("Setting state to arrived at {}.", destination);
        return new ElevatorState(
                INACTIVE,
                destination,
                empty(),
                empty(),
                empty()
        );
    }

    public static ElevatorState setNewDestinationState(final ElevatorState currentState, final ElevatorDestination newDestination) {
        final ElevatorDestination previousDestination = currentState.getNextDestination().orElse(currentState.getPreviousDestination());

        logger.info("Setting state to travel from destination {} to {}.", previousDestination, newDestination);

        final long numberOfFloors = Math.abs(newDestination.getDestinationId() - previousDestination.getDestinationId());
        final LocalDateTime now = LocalDateTime.now();
        return new ElevatorState(
                IN_MOTION,
                previousDestination,
                Optional.of(newDestination),
                decideDirection(previousDestination, newDestination),
                Optional.of(
                        TravelTime
                                .builder()
                                .startTime(now)
                                .calculatedEndTime(now.plus(numberOfFloors * TIME_PER_FLOOR_IN_MILLIS, MILLIS))
                                .numberOfFloorsToTravel(Math.abs(numberOfFloors))
                                .build()
                )
        );
    }

    static ElevatorState toStoppedState(final ElevatorState currentState) {
        if (!currentState.getMotionState().equals(IN_MOTION)) {
            logger.info("Elevator is not in motion");
            return currentState;
        }
        logger.info("Setting stopped state");
        final LocalDateTime now = LocalDateTime.now();

        final LocalDateTime arrival = currentState.getTravelTime().map(TravelTime::getCalculatedEndTime).orElse(now);
        final Duration remainingTime = Duration.between(now, arrival);
        long remainingFloors = remainingTime.get(SECONDS) / (TIME_PER_FLOOR_IN_MILLIS / 1000);

        logger.info("{} remaining floors when stopped with remaining time {}.", remainingFloors, remainingTime);

        return new ElevatorState(
                STOPPED,
                currentState.getPreviousDestination(),
                currentState.getNextDestination(),
                currentState.getMovementDirection(),
                Optional.of(
                        TravelTime
                                .builder()
                                .startTime(now)
                                .calculatedEndTime(now)
                                .numberOfFloorsToTravel(remainingFloors)
                                .build()
                )
        );
    }

    static ElevatorState toResumedState(final ElevatorState currentState) {
        if (!currentState.getMotionState().equals(STOPPED)) {
            logger.info("State is not stopped, nothing to resume from");
            return currentState;
        }
        logger.info("Setting resumed state");
        currentState.getTravelTime().ifPresent(travelTime -> logger.info("Resuming with {} floors left", travelTime.getNumberOfFloorsToTravel()));
        final LocalDateTime now = LocalDateTime.now();

        return new ElevatorState(
                IN_MOTION,
                currentState.getPreviousDestination(),
                currentState.getNextDestination(),
                currentState.getMovementDirection(),
                currentState.getTravelTime().map(
                        travelTime ->
                                TravelTime
                                        .builder()
                                        .startTime(now)
                                        .calculatedEndTime(now.plus(travelTime.getNumberOfFloorsToTravel() * TIME_PER_FLOOR_IN_MILLIS, MILLIS))
                                        .numberOfFloorsToTravel(travelTime.getNumberOfFloorsToTravel())
                                        .build()
                )
        );
    }

    private static Optional<Direction> decideDirection(
            final ElevatorDestination previousDestination,
            final ElevatorDestination nextDestination
    ) {
        switch (nextDestination.compareTo(previousDestination)) {
            case -1:
                return Optional.of(DOWN);
            case 0:
                return empty();
            case 1:
                return Optional.of(UP);
            default:
                throw new IllegalStateException("Cannot decide direction for elevator");
        }
    }
}
