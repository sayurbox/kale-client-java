package com.sayurbox.kale.config;

import static java.util.Objects.requireNonNull;

public class KaleConfig {

    private final String baseUrl;
    private final Long executionTimeout;
    private final boolean logEnabled;
    private final CircuitBreakerParams circuitBreakerParams;

    private KaleConfig(String baseUrl,
                       Long executionTimeout,
                       CircuitBreakerParams circuitBreakerParams,
                       boolean logEnabled) {
        this.baseUrl = requireNonNull(baseUrl);
        this.circuitBreakerParams = circuitBreakerParams;
        this.executionTimeout = executionTimeout;
        this.logEnabled = logEnabled;
    }

    public CircuitBreakerParams getCircuitBreakerParams() {
        return circuitBreakerParams;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Long getExecutionTimeout() {
        return executionTimeout;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public static class Builder {

        private String baseUrl;
        private boolean logEnabled = false;
        private Integer executionTimeout = 5000;
        private boolean circuitBreakerEnabled = true;
        private Integer failureVolumeThreshold = 10;
        private Integer slowCallDurationThreshold = 1000;
        private Integer waitDuration = 20000;

        public Builder() {
        }

        public Builder withBaseUrl(String url) {
            this.baseUrl = url;
            return this;
        }

        public Builder withExecutionTimeout(int executionTimeout) {
            this.executionTimeout = executionTimeout;
            return this;
        }

        public Builder withCircuitBreakerEnabled(boolean circuitBreakerEnabled) {
            this.circuitBreakerEnabled = circuitBreakerEnabled;
            return this;
        }

        public Builder withCircuitBreakerFailureVolumeThreshold(int failureVolumeThreshold) {
            this.failureVolumeThreshold = failureVolumeThreshold;
            return this;
        }

        public Builder withCircuitBreakerSlowResponseThreshold(int slowCallDurationThreshold) {
            this.slowCallDurationThreshold = slowCallDurationThreshold;
            return this;
        }

        public Builder withCircuitBreakerWaitDurationOpenState(int waitDuration) {
            this.waitDuration = waitDuration;
            return this;
        }

        public Builder withLoggerEnabled(boolean loggerEnabled) {
            this.logEnabled = loggerEnabled;
            return this;
        }

        public KaleConfig build() {
            CircuitBreakerParams circuitBreakerParams = new CircuitBreakerParams(
                    circuitBreakerEnabled,
                    failureVolumeThreshold,
                    slowCallDurationThreshold.longValue(),
                    waitDuration.longValue());
            return new KaleConfig(baseUrl,
                    executionTimeout.longValue(),
                    circuitBreakerParams,
                    logEnabled);
        }
    }

}
