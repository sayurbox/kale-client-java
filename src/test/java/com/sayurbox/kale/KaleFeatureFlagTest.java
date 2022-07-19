package com.sayurbox.kale;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sayurbox.kale.config.KaleConfig;
import com.sayurbox.kale.featureflag.FeatureFlagClient;
import com.sayurbox.kale.featureflag.FeatureFlagClientImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

// End-to-end test
public class KaleFeatureFlagTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9797);

    @Before
    public void before() {
        WireMock.reset();
    }

    @Test
    public void allocateV1Test() {
        String userId = "cSExFZtCP8ee9cfr7yJVVmcsi5A3";
        String featureId = "ae2802be-86b6-47dd-a17a-864e4c76b49d";
        stubFor(get(urlEqualTo(String.format("/v1/featureflag/allocation/%s/%s", userId, featureId )))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":true}}")
                        .withFixedDelay(1000)
                ));

        KaleConfig config = new KaleConfig.Builder()
                .withBaseUrl("http://localhost:9797")
                .withLoggerEnabled(false)
                .withExecutionTimeout(1000)
                .withCircuitBreakerEnabled(true)
                .withCircuitBreakerFailureVolumeThreshold(5)
                .withCircuitBreakerSlowResponseThreshold(200)
                .withCircuitBreakerWaitDurationOpenState(5_000)
                .build();

        FeatureFlagClient ff = new FeatureFlagClientImpl(config);
        for (int i = 1; i <= 10; i++) {
            // check if user is allocated to a feature ?
            boolean isAllocate = ff.isAllocate(featureId, userId);
            if (isAllocate) {
                System.out.println("user is allocated");
            } else {
                System.out.println("user is not allocated");
            }
        }
        try {
            // sleep to simulate circuit breaker waiting for open state
            Thread.sleep(6_000);
        } catch (InterruptedException e) {
        }

        System.out.println("\n\nSecond batch:");
        for (int i = 1; i <= 20; i++) {
            boolean isAllocate = ff.isAllocate(featureId, userId);
            if (isAllocate) {
                System.out.println("user is allocated");
            } else {
                System.out.println("user is not allocated");
            }
        }
    }

    @Test
    public void allocateV2Test() {
        String userId = "cSExFZtCP8ee9cfr7yJVVmcsi5A3";
        String featureName = "sayurkilat";
        stubFor(get(urlEqualTo(String.format("/v2/featureflag/allocation/%s/%s", userId, featureName )))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":true}}")
                        .withFixedDelay(1000)
                ));

        KaleConfig config = new KaleConfig.Builder()
                .withBaseUrl("http://localhost:9797")
                .withLoggerEnabled(false)
                .withExecutionTimeout(1000)
                .withCircuitBreakerEnabled(true)
                .withCircuitBreakerFailureVolumeThreshold(5)
                .withCircuitBreakerSlowResponseThreshold(200)
                .withCircuitBreakerWaitDurationOpenState(5_000)
                .build();

        FeatureFlagClient ff = new FeatureFlagClientImpl(config);
        for (int i = 1; i <= 10; i++) {
            // check if user is allocated to a feature ?
            boolean isAllocate = ff.isAllocateV2(featureName, userId);
            if (isAllocate) {
                System.out.println("user is allocated");
            } else {
                System.out.println("user is not allocated");
            }
        }
        try {
            // sleep to simulate circuit breaker waiting for open state
            Thread.sleep(6_000);
        } catch (InterruptedException e) {
        }

        System.out.println("\n\nSecond batch:");
        for (int i = 1; i <= 20; i++) {
            boolean isAllocate = ff.isAllocateV2(featureName, userId);
            if (isAllocate) {
                System.out.println("user is allocated");
            } else {
                System.out.println("user is not allocated");
            }
        }
    }
}
