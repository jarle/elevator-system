package com.github.jarle.elevator_entity.domain;

import java.util.Optional;

import com.github.jarle.elevator_domain.Direction;
import com.github.jarle.elevator_domain.ElevatorDestination;
import com.github.jarle.elevator_domain.TravelTime;
import lombok.Data;
import lombok.NonNull;

/**
 * A representation of the current state of an elevator.
 * State transitions should be delegated to the {@link ElevatorStateTransformer} in order to ensure correct state transitions.
 */
@Data
public class ElevatorState {
    @NonNull
    private final MotionState motionState;
    @NonNull
    private final ElevatorDestination previousDestination;
    @NonNull
    private final Optional<ElevatorDestination> nextDestination;
    @NonNull
    private final Optional<Direction> movementDirection;
    @NonNull
    private final Optional<TravelTime> travelTime;
}
