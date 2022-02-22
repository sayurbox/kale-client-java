package com.sayurbox.kale.config;

public class CircuitBreakerParams {

    private boolean enabled;
    // in milliseconds
    private Long executionTimeout;
    private Integer slidingWindowSize;
    // in milliseconds
    private Long slowCallDurationThreshold;
    // in milliseconds
    private Long waitDurationInOpenState;

    public CircuitBreakerParams(boolean enabled,
                                Long executionTimeout,
                                Integer slidingWindowSize,
                                Long slowCallDurationThreshold,
                                Long waitDurationInOpenState) {
        this.enabled = enabled;
        this.executionTimeout = executionTimeout;
        this.slidingWindowSize = slidingWindowSize;
        this.slowCallDurationThreshold = slowCallDurationThreshold;
        this.waitDurationInOpenState = waitDurationInOpenState;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Long getExecutionTimeout() {
        return executionTimeout;
    }

    public Integer getSlidingWindowSize() {
        return slidingWindowSize;
    }

    public Long getSlowCallDurationThreshold() {
        return slowCallDurationThreshold;
    }

    public Long getWaitDurationInOpenState() {
        return waitDurationInOpenState;
    }
}
