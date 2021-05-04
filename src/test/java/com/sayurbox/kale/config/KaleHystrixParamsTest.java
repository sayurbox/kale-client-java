package com.sayurbox.kale.config;

import org.junit.Assert;
import org.junit.Test;

public class KaleHystrixParamsTest {

    @Test
    public void hystrixParam() {
        KaleHystrixParams params =
                new KaleHystrixParams(1000, 400, 20, 100, 500);
        Assert.assertEquals(1000, params.getExecutionTimeout().longValue());
        Assert.assertEquals(400, params.getCircuitBreakerSleepWindow().longValue());
        Assert.assertEquals(20, params.getCircuitBreakerRequestVolumeThreshold().longValue());
        Assert.assertEquals(100, params.getMetricRollingStatisticalWindow().longValue());
        Assert.assertEquals(500, params.getMetricsHealthSnapshotInterval().longValue());
        Assert.assertEquals("kale-hystrix-client", params.getHystrixCommandGroupKey().name());
    }

}
