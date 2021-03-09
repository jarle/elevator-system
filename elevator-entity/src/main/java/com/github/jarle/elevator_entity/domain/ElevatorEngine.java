package com.github.jarle.elevator_entity.domain;

import static com.github.jarle.elevator_domain.Direction.DOWN;
import static com.github.jarle.elevator_domain.Direction.UP;
import static com.github.jarle.elevator_entity.domain.EngineState.MOVING_DOWN;
import static com.github.jarle.elevator_entity.domain.EngineState.MOVING_UP;
import static com.github.jarle.elevator_entity.domain.EngineState.STOPPED;
import static java.util.Optional.empty;

import java.util.Optional;
import java.util.concurrent.Callable;

import com.github.jarle.elevator_domain.Direction;
import com.github.jarle.elevator_domain.TravelTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Represents the work done by an {@link ElevatorEngine}.
 * The {@link ElevatorEngine} is instructed with a {@link TravelTime} and {@link Direction}.
 * {@link EngineState} will be set to {@link EngineState#STOPPED} if interrupted.
 */
@Component
public class ElevatorEngine implements Callable<EngineState> {
    Logger logger = LoggerFactory.getLogger(ElevatorEngine.class);

    private EngineState engineState = STOPPED;
    private Optional<Direction> direction = empty();
    private Optional<TravelTime> travelTime = empty();

    ElevatorEngine withDirection(Optional<Direction> direction) {
        this.direction = direction;
        return this;
    }

    ElevatorEngine withTravelTime(final Optional<TravelTime> travelTime) {
        this.travelTime = travelTime;
        return this;
    }

    @Override
    public EngineState call() {
        // simulating engine movement
        if (direction.isEmpty() || travelTime.isEmpty()) {
            this.engineState = STOPPED;
            return this.engineState;
        }
        else if (direction.get().equals(UP)) {
            this.engineState = MOVING_UP;
        }
        else if (direction.get().equals(DOWN)) {
            this.engineState = MOVING_DOWN;
        }

        final long duration = travelTime.get().duration();
        logger.info("Elevator engine is: {} {} floor(s) ({} seconds)", this.engineState, travelTime.get().getNumberOfFloorsToTravel(), duration / 1000);

        try {
            Thread.sleep(duration);
            this.engineState = STOPPED;
            return this.engineState;
        } catch (InterruptedException e) {
            logger.info("Engine was stopped");
            this.engineState = STOPPED;
            return this.engineState;
        }
    }
}
