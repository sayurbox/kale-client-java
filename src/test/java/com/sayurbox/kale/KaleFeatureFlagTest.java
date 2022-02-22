package com.sayurbox.kale;

import com.sayurbox.kale.config.CircuitBreakerParams;
import com.sayurbox.kale.config.KaleConfig;
import com.sayurbox.kale.featureflag.FeatureFlagClient;
import com.sayurbox.kale.featureflag.FeatureFlagClientImpl;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class KaleFeatureFlagTest {

    @Test
    public void featureFlagTest() {

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()

                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .slowCallRateThreshold(70.0f)
                .slowCallDurationThreshold(Duration.ofMillis(100))
                .waitDurationInOpenState(Duration.ofSeconds(15))
                .recordExceptions(IOException.class, TimeoutException.class)
                .build();


        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);
        CircuitBreaker circuitBreaker = circuitBreakerRegistry
                .circuitBreaker("kaleApiFeatureflag");

        KaleConfig config = new KaleConfig.Builder()
                .withBaseUrl("http://localhost:8080") // required & mandatory
                .withHystrixExecutionTimeout(2000)  // optional (default is 5000ms)
                .withHystrixCircuitBreakerSleepWindow(500) // optional
                .withHystrixCircuitBreakerRequestVolumeThreshold(10)  // optional
                .withHystrixRollingStatisticalWindow(500) // optional
                .withHystrixHealthSnapshotInterval(500) // optional
                .build();

        CircuitBreakerParams circuitBreakerParams = new CircuitBreakerParams(true,
                500L,
                5,
                500L,
                15_000L);

        config = new KaleConfig("http://localhost:5000", circuitBreakerParams);

        FeatureFlagClient ff = new FeatureFlagClientImpl(config);
        for (int i = 1; i <= 10; i++) {
            // check if user is allocated to a feature ?
            boolean isAllocate = ff.isAllocate("ae2802be-86b6-47dd-a17a-864e4c76b49d",
                    "cSExFZtCP8ee9cfr7yJVVmcsi5A3");
            if (isAllocate) {
                System.out.println("user is allocated");
            } else {
                System.out.println("user is not allocated");
            }


        }
        try {
            Thread.sleep(20_000);
        } catch (InterruptedException e) {
        }
        System.out.println("\n\nSecond batch >>>>");
        for (int i = 1; i <= 20; i++) {
            // check if user is allocated to a feature ?
            boolean isAllocate = ff.isAllocate("ae2802be-86b6-47dd-a17a-864e4c76b49d",
                    "cSExFZtCP8ee9cfr7yJVVmcsi5A3");
            if (isAllocate) {
                System.out.println("user is allocated");
            } else {
                System.out.println("user is not allocated");
            }

        }

        try {
            Thread.sleep(20_000);
        } catch (InterruptedException e) {
        }
        System.out.println("\n\nThird batch >>>>");
        for (int i = 1; i <= 30; i++) {
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

}
