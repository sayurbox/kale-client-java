package com.sayurbox.kale.config;

/**
 * @param slowCallDurationThreshold in milliseconds
 * @param waitDurationInOpenState   in milliseconds
 */
public record CircuitBreakerParams(boolean enabled, Integer slidingWindowSize, Long slowCallDurationThreshold,
                                   Long waitDurationInOpenState) {

}
