package com.github.jarle.elevator_domain;

import java.time.Duration;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

/**
 * Represents travel time between a given number of {@link ElevatorDestination}s.
 * Represented as a time period with a calculated end time based on the seconds taken per floor travelled.
 * Remaining duration can be calculated by passing the current time to {@link #remaining(LocalDateTime)}.
 */
@Data
@Builder
@JsonDeserialize(builder = TravelTime.TravelTimeBuilder.class)
public class TravelTime {
    public static final Long TIME_PER_FLOOR_IN_MILLIS = 1000L;
    private final LocalDateTime startTime;
    private final LocalDateTime calculatedEndTime;
    private final Long numberOfFloorsToTravel;

    public Long duration() {
        return numberOfFloorsToTravel * TIME_PER_FLOOR_IN_MILLIS;
    }

    public Long remaining(final LocalDateTime now) {
        return Duration.between(now, calculatedEndTime).toSeconds();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class TravelTimeBuilder {

    }
}
