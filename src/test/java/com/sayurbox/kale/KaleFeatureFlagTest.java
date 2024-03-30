package com.sayurbox.kale;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sayurbox.kale.config.KaleConfig;
import com.sayurbox.kale.featureflag.FeatureFlagClient;
import com.sayurbox.kale.featureflag.FeatureFlagClientImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.awaitility.Awaitility.await;

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

        // sleep to simulate circuit breaker waiting for open state
        await().atMost(6_000, TimeUnit.MILLISECONDS);

        System.out.println("\n\nSecond batch:");
        for (int i = 1; i <= 20; i++) {
            boolean isAllocate = ff.isAllocate(featureId, userId);
            Assert.assertFalse(isAllocate);
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

        // sleep to simulate circuit breaker waiting for open state
        await().atMost(6_000, TimeUnit.MILLISECONDS);

        System.out.println("\n\nSecond batch:");
        for (int i = 1; i <= 20; i++) {
            boolean isAllocate = ff.isAllocateV2(featureName, userId);
            Assert.assertFalse(isAllocate);
        }
    }

    @Test
    public void getAllocatedFeatureNamesTest() {
        String userId = "cSExFZtCP8ee9cfr7yJVVmcsi5A3";
        stubFor(get(urlEqualTo(String.format("/v2/featureflag/allocation/%s", userId)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"features\":[{\"feature_id\":\"053e06de-a3d5-4b19-8ff5-346fb3aed8d7\"," +
                                "\"feature_name\":\"www-quick-checkout-navbar-v2\"}]}}")
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
            ff.getAllocatedFeatureNames(userId);
        }

        // sleep to simulate circuit breaker waiting for open state
        await().atMost(6_000, TimeUnit.MILLISECONDS);

        for (int i = 1; i <= 20; i++) {
            Set<String> featureNames = ff.getAllocatedFeatureNames(userId);
            Assert.assertTrue(featureNames.isEmpty());
        }
    }
}
