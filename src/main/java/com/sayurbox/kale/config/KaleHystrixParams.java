package com.sayurbox.kale.config;

import com.netflix.hystrix.HystrixCommandGroupKey;

public class KaleHystrixParams {

    private final Integer executionTimeout;
    private final Integer circuitBreakerSleepWindow;
    private final Integer circuitBreakerRequestVolumeThreshold;
    private final Integer metricRollingStatisticalWindow;
    private final Integer metricsHealthSnapshotInterval;

    public KaleHystrixParams(Integer executionTimeout,
                             Integer circuitBreakerSleepWindow,
                             Integer circuitBreakerRequestVolumeThreshold,
                             Integer metricRollingStatisticalWindow,
                             Integer metricsHealthSnapshotInterval) {
        this.executionTimeout = executionTimeout;
        this.circuitBreakerSleepWindow = circuitBreakerSleepWindow;
        this.circuitBreakerRequestVolumeThreshold = circuitBreakerRequestVolumeThreshold;
        this.metricRollingStatisticalWindow = metricRollingStatisticalWindow;
        this.metricsHealthSnapshotInterval = metricsHealthSnapshotInterval;
    }

    public Integer getExecutionTimeout() {
        return executionTimeout;
    }

    public Integer getCircuitBreakerSleepWindow() {
        return circuitBreakerSleepWindow;
    }

    public Integer getCircuitBreakerRequestVolumeThreshold() {
        return circuitBreakerRequestVolumeThreshold;
    }

    public Integer getMetricRollingStatisticalWindow() {
        return metricRollingStatisticalWindow;
    }

    public Integer getMetricsHealthSnapshotInterval() {
        return metricsHealthSnapshotInterval;
    }

    public HystrixCommandGroupKey getHystrixCommandGroupKey() {
        return HystrixCommandGroupKey.Factory.asKey("kale-hystrix-client");
    }

}
