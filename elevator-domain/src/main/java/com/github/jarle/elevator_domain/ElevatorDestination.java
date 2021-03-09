package com.github.jarle.elevator_domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Represents an elevator destination such as a building floor
 */
@Data
@Builder
@JsonDeserialize(builder = ElevatorDestination.ElevatorDestinationBuilder.class)
public class ElevatorDestination implements Comparable<ElevatorDestination> {
    @NonNull
    private Long destinationId;

    @Override
    public int compareTo(final ElevatorDestination that) {
        return this.destinationId.compareTo(that.destinationId);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ElevatorDestinationBuilder {

    }
}
