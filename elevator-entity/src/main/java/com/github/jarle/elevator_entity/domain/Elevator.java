package com.github.jarle.elevator_entity.domain;

import static com.github.jarle.elevator_domain.Direction.NONE;
import static com.github.jarle.elevator_entity.domain.ElevatorStateTransformer.arrivedAtDestinationState;
import static com.github.jarle.elevator_entity.domain.ElevatorStateTransformer.initialState;
import static com.github.jarle.elevator_entity.domain.ElevatorStateTransformer.setNewDestinationState;
import static com.github.jarle.elevator_entity.domain.ElevatorStateTransformer.toResumedState;
import static com.github.jarle.elevator_entity.domain.ElevatorStateTransformer.toStoppedState;
import static com.github.jarle.elevator_entity.domain.EngineState.STOPPED;
import static com.github.jarle.elevator_entity.domain.MotionState.INACTIVE;
import static com.github.jarle.elevator_entity.domain.MotionState.IN_MOTION;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.github.jarle.elevator_domain.Direction;
import com.github.jarle.elevator_domain.ElevatorDestination;
import com.github.jarle.elevator_domain.TravelTime;
import com.github.jarle.elevator_entity.service.ElevatorScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Represents an elevator controlling a given {@link ElevatorEngine}.
 * The elevator will continuously poll the {@link ElevatorScheduleService} for a new
 * {@link ElevatorDestination} when {@link ElevatorState} is {@link MotionState#INACTIVE}
 */
@Component
public class Elevator implements Callable<Void> {
    Logger logger = LoggerFactory.getLogger(Elevator.class);


    final ElevatorScheduleService scheduleService;
    final ElevatorEngine elevatorEngine;
    ElevatorState currentState;
    private final ExecutorService executorService;
    private Future<EngineState> engineExecution = completedFuture(STOPPED);

    @Autowired
    public Elevator(final ElevatorScheduleService scheduleService, final ElevatorEngine elevatorEngine) {
        this.executorService = newSingleThreadExecutor();
        this.scheduleService = scheduleService;
        this.elevatorEngine = elevatorEngine;
        this.currentState = initialState();
    }

    @Override
    public Void call() {
        while (true) {
            try {
                // polling loop
                executeControlLoop();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void stop() {
        this.engineExecution.cancel(true);
        this.currentState = toStoppedState(this.currentState);
    }

    public void resume() {
        this.currentState = toResumedState(this.currentState);
        startElevatorEngineExecution(this.currentState.getTravelTime());
    }

    private void executeControlLoop() {
        if (this.currentState.getMotionState().equals(MotionState.STOPPED) || this.engineExecution.isCancelled()) {
            return;
        }
        if (this.engineExecution.isDone()) {
            if (this.currentState.getMotionState().equals(IN_MOTION)) {
                logger.info("Engine execution done, unscheduling destination and setting inactive state.");
                this.currentState = arrivedAtDestinationState(this.currentState);
                this.scheduleService.unScheduleDestination(this.currentState.getPreviousDestination());
            }

            final Optional<ElevatorDestination> nextDestination = scheduleService.getNextDestination();
            if (this.currentState.getMotionState().equals(INACTIVE) && nextDestination.isPresent()) {
                logger.info("Moving to new destination {}", nextDestination.get());
                this.currentState = setNewDestinationState(this.currentState, nextDestination.get());
                startElevatorEngineExecution(this.currentState.getTravelTime());
            }
        }
    }

    private void startElevatorEngineExecution(final Optional<TravelTime> travelTime) {
        this.engineExecution = executorService.submit(
                this.elevatorEngine
                        .withDirection(this.currentState.getMovementDirection())
                        .withTravelTime(travelTime)
        );
    }

    public Optional<TravelTime> remainingTimeToDestination() {
        return this.currentState.getTravelTime();
    }

    public Direction currentDirection() {
        return this.currentState.getMovementDirection().orElse(NONE);
    }
}
