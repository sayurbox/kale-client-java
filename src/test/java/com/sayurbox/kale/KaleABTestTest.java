package com.sayurbox.kale;

import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.config.KaleConfig;

import com.sayurbox.kale.abtest.ABTestClient;
import com.sayurbox.kale.abtest.ABTestClientImpl;

import java.util.List;

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

        GetUniverseAllocationResponse universeAllocation = abTest.getUniverseAllocation(
                "qFA88l70U4RxscuJZFUwEZpHUUUF", "0b95519c-1b32-47fe-aa8c-55cb65d6f8c4"
        );
        List<GetUniverseAllocationResponse> allUniverseAllocations = abTest.getAllUniverseAllocations(
                "qFA88l70U4RxscuJZFUwEZpHUUUF"
        );
        System.out.println(universeAllocation);
        System.out.printf("Universe: %s Experiment: %s Variant: %s Configs: %s User: %s\n",
                universeAllocation.getUniverseId(),
                universeAllocation.getExperimentId(),
                universeAllocation.getVariantId(),
                universeAllocation.getConfigs(),
                universeAllocation.getUserId()
        );

        System.out.println(allUniverseAllocations);
        for (GetUniverseAllocationResponse allocation : allUniverseAllocations) {
            System.out.printf("\nUniverse: %s Experiment: %s Variant: %s Configs: %s User: %s",
                    allocation.getUniverseId(),
                    allocation.getExperimentId(),
                    allocation.getVariantId(),
                    allocation.getConfigs(),
                    allocation.getUserId()
            );
        }
    }
}
