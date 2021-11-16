package com.sayurbox.kale;

import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.config.KaleConfig;

import com.sayurbox.kale.abtest.ABTestClient;
import com.sayurbox.kale.abtest.ABTestClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KaleABTestTest {

    private static final Logger logger = LoggerFactory.getLogger(KaleABTestTest.class);

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
        logger.info("Universe: {} Experiment: {} Variant: {} Configs: {} User: {}\n",
                universeAllocation.getUniverseId(),
                universeAllocation.getExperimentId(),
                universeAllocation.getVariantId(),
                universeAllocation.getConfigs(),
                universeAllocation.getUserId()
        );

        for (GetUniverseAllocationResponse allocation : allUniverseAllocations) {
            logger.info("\nUniverse: {} Experiment: {} Variant: {} Configs: {} User: {}",
                    allocation.getUniverseId(),
                    allocation.getExperimentId(),
                    allocation.getVariantId(),
                    allocation.getConfigs(),
                    allocation.getUserId()
            );
        }
    }
}
