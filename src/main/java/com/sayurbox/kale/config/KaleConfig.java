package com.sayurbox.kale.config;

import static java.util.Objects.requireNonNull;

public class KaleConfig {

    private final String baseUrl;

    private final KaleHystrixParams hystrixParams;

    public KaleConfig(String baseUrl, KaleHystrixParams hystrixParams) {
        this.baseUrl = requireNonNull(baseUrl);
        this.hystrixParams = hystrixParams;
    }

    public KaleHystrixParams getHystrixParams() {
        return hystrixParams;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public static class Builder {

        private String baseUrl;
        private Integer hystrixExecutionTimeout = 5000;
        private Integer hystrixCircuitBreakerSleepWindow = 1000;
        private Integer hystrixCircuitBreakerRequestVolumeThreshold = 10;
        private Integer hystrixRollingStatisticalWindow = 10000;
        private Integer hystrixHealthSnapshotInterval = 500;

        public Builder() {
        }

        public Builder withBaseUrl(String url) {
            this.baseUrl = url;
            return this;
        }

        public Builder withHystrixExecutionTimeout(int hystrixExecutionTimeout) {
            this.hystrixExecutionTimeout = hystrixExecutionTimeout;
            return this;
        }

        public Builder withHystrixCircuitBreakerSleepWindow(int hystrixCircuitBreakerSleepWindow) {
            this.hystrixCircuitBreakerSleepWindow = hystrixCircuitBreakerSleepWindow;
            return this;
        }

        public Builder withHystrixCircuitBreakerRequestVolumeThreshold(
                int hystrixCircuitBreakerRequestVolumeThreshold) {
            this.hystrixCircuitBreakerRequestVolumeThreshold = hystrixCircuitBreakerRequestVolumeThreshold;
            return this;
        }

        public Builder withHystrixRollingStatisticalWindow(int hystrixRollingStatisticalWindow) {
            this.hystrixRollingStatisticalWindow = hystrixRollingStatisticalWindow;
            return this;
        }

        public Builder withHystrixHealthSnapshotInterval(int hystrixHealthSnapshotInterval) {
            this.hystrixHealthSnapshotInterval = hystrixHealthSnapshotInterval;
            return this;
        }

        public KaleConfig build() {
            KaleHystrixParams hystrixParams = new KaleHystrixParams(hystrixExecutionTimeout,
                    hystrixCircuitBreakerSleepWindow,
                    hystrixCircuitBreakerRequestVolumeThreshold,
                    hystrixRollingStatisticalWindow,
                    hystrixHealthSnapshotInterval);
            return new KaleConfig(baseUrl, hystrixParams);
        }
    }

}
