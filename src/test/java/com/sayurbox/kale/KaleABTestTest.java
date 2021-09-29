package com.sayurbox.kale;

import com.sayurbox.kale.config.KaleConfig;

import java.util.List;

import com.google.gson.JsonObject;
import com.sayurbox.kale.abtest.ABTestClient;
import com.sayurbox.kale.abtest.ABTestClientImpl;

public class KaleABTestTest {

    public void abTestTest(){
        KaleConfig config = new KaleConfig.Builder()
                .withBaseUrl("https://kale-api-consumer-dev-ina.apps.aws.sayurbox.io") // required & mandatory
                .withHystrixExecutionTimeout(5000)  // optional (default is 5000ms)
                .withHystrixCircuitBreakerSleepWindow(500) // optional
                .withHystrixCircuitBreakerRequestVolumeThreshold(10)  // optional
                .withHystrixRollingStatisticalWindow(500) // optional
                .withHystrixHealthSnapshotInterval(500) // optional
                .build();
        ABTestClient abTest = new ABTestClientImpl(config);
    
        JsonObject universeAllocation = abTest.getUniverseAllocation("qFA88l70U4RxscuJZFUwEZpHUUUF", "0b95519c-1b32-47fe-aa8c-55cb65d6f8c4");
        List<JsonObject> allUniverseAllocations = abTest.getAllUniverseAllocations("qFA88l70U4RxscuJZFUwEZpHUUUF");
        System.out.println(universeAllocation);
        System.out.println(allUniverseAllocations);
    }
}
