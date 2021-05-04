package com.sayurbox.kale;

import com.sayurbox.kale.config.KaleConfig;
import com.sayurbox.kale.featureflag.FeatureFlagClient;
import com.sayurbox.kale.featureflag.FeatureFlagClientImpl;

public class KaleFeatureFlagTest {

    public void featureFlagTest() {
        KaleConfig config = new KaleConfig.Builder()
                .withBaseUrl("http://localhost:8080") // required & mandatory
                .withHystrixExecutionTimeout(2000)  // optional (default is 5000ms)
                .withHystrixCircuitBreakerSleepWindow(500) // optional
                .withHystrixCircuitBreakerRequestVolumeThreshold(10)  // optional
                .withHystrixRollingStatisticalWindow(500) // optional
                .withHystrixHealthSnapshotInterval(500) // optional
                .build();
        FeatureFlagClient ff = new FeatureFlagClientImpl(config);
        // check if user is allocated to a feature ?
        boolean isAllocate = ff.isAllocate("ae2802be-86b6-47dd-a17a-864e4c76b49d",
                "cSExFZtCP8ee9cfr7yJVVmcsi5A3");
        if (isAllocate) {
            System.out.println("user is allocated");
        } else {
            System.out.println("user is not allocated");
        }
    }

}
