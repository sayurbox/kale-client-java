package com.sayurbox.kale.config;

import org.junit.Assert;
import org.junit.Test;

public class KaleConfigTest {

    @Test
    public void kaleConfig() {
        KaleConfig.Builder builder = new KaleConfig.Builder().withBaseUrl("http://localhost:90");
        builder.withHystrixExecutionTimeout(500);
        builder.withHystrixCircuitBreakerRequestVolumeThreshold(40);
        builder.withHystrixCircuitBreakerSleepWindow(250);
        builder.withHystrixCircuitBreakerRequestVolumeThreshold(70);
        builder.withHystrixRollingStatisticalWindow(700);
        builder.withHystrixHealthSnapshotInterval(140);
        KaleConfig cfg = builder.build();
        Assert.assertEquals("http://localhost:90", cfg.getBaseUrl());
        Assert.assertNotNull(cfg.getHystrixParams());
    }

}
