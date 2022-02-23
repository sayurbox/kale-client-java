package com.sayurbox.kale.config;

import org.junit.Assert;
import org.junit.Test;

public class KaleConfigTest {

    @Test
    public void kaleConfig() {
        KaleConfig.Builder builder = new KaleConfig.Builder().withBaseUrl("http://localhost:90");
        builder.withLoggerEnabled(true);
        builder.withExecutionTimeout(1000);
        builder.withCircuitBreakerEnabled(true);
        builder.withCircuitBreakerFailureVolumeThreshold(5);
        builder.withCircuitBreakerSlowResponseThreshold(500);
        builder.withCircuitBreakerWaitDurationOpenState(15000);
        KaleConfig cfg = builder.build();
        Assert.assertEquals("http://localhost:90", cfg.getBaseUrl());
        Assert.assertTrue(cfg.isLogEnabled());
        Assert.assertEquals(1000L, cfg.getExecutionTimeout().longValue());
        Assert.assertNotNull(cfg.getCircuitBreakerParams());
    }

}
