package com.sayurbox.kale.featureflag.command;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sayurbox.kale.featureflag.client.GetAllocatedFeaturesResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class GetAllocatedFeatureNamesCommandTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9393);

    @Before
    public void before() {
        WireMock.reset();
    }

    @Test
    public void GetAllocatedFeatureNamesCommand_TimeoutRequest_ShouldFallbackFalse() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"features\":[{\"feature_id\":\"053e06de-a3d5-4b19-8ff5-346fb3aed8d7\"," +
                                "\"feature_name\":\"www-quick-checkout-navbar-v2\"},{\"feature_id\":" +
                                "\"06ea2c76-f6c0-4500-964e-4920e902f11b\",\"feature_name\":\"jenius-callback\"}]}}")
                        .withFixedDelay(1000)
                ));

        GetAllocatedFeatureNamesCommand cmd = new GetAllocatedFeatureNamesCommand(provideCircuitBreaker(),
                provideOkHttpClient(), true, "http://localhost:9393", "user-003");
        GetAllocatedFeaturesResponse actual = cmd.execute();
        Assert.assertNotNull(actual);
        Assert.assertEquals(0, actual.getFeatures().size());
    }

    @Test
    public void GetAllocatedFeatureNamesCommand_Non200Response() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-001"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"code\":1234,\"message\":\"invalid data\",\"type\":\"Error\"}}")
                ));

        GetAllocatedFeatureNamesCommand cmd = new GetAllocatedFeatureNamesCommand(provideCircuitBreaker(),
                provideOkHttpClient(), true, "http://localhost:9393", "user-001");
        GetAllocatedFeaturesResponse actual = cmd.execute();
        Assert.assertNotNull(actual);
        Assert.assertEquals(0, actual.getFeatures().size());
    }

    @Test
    public void GetAllocatedFeatureNamesCommand_HasList() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-001"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"features\":[{\"feature_id\":\"053e06de-a3d5-4b19-8ff5-346fb3aed8d7\"," +
                                "\"feature_name\":\"www-quick-checkout-navbar-v2\"},{\"feature_id\":" +
                                "\"06ea2c76-f6c0-4500-964e-4920e902f11b\",\"feature_name\":\"jenius-callback\"}]}}")
                ));

        GetAllocatedFeatureNamesCommand cmd = new GetAllocatedFeatureNamesCommand(provideCircuitBreaker(),
                provideOkHttpClient(), true, "http://localhost:9393", "user-001");
        GetAllocatedFeaturesResponse actual = cmd.execute();
        Assert.assertNotNull(actual);
        Assert.assertEquals(2, actual.getFeatures().size());
    }

    @Test
    public void GetAllocatedFeatureNamesCommand_CircuitBreakerOpen_ShouldFallbackFalse() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"features\":[{\"feature_id\":\"053e06de-a3d5-4b19-8ff5-346fb3aed8d7\"," +
                                "\"feature_name\":\"www-quick-checkout-navbar-v2\"},{\"feature_id\":" +
                                "\"06ea2c76-f6c0-4500-964e-4920e902f11b\",\"feature_name\":\"jenius-callback\"}]}}")
                        .withFixedDelay(200)
                ));

        CircuitBreaker circuitBreaker = provideCircuitBreaker();
        OkHttpClient okHttpClient = provideOkHttpClient();
        int[] actualResults = new int[10];
        int[] expectedResult =
                new int[] {2, 2, 2, 2, 2, 0, 0, 0, 0, 0};
        boolean[] actualCircuitClosed = new boolean[10];
        boolean[] expectedCircuitClosed =
                new boolean[] {true, true, true, true, false, false, false, false, false, false};

        for (int i = 0; i < 10; i++) {
            GetAllocatedFeatureNamesCommand cmd = new GetAllocatedFeatureNamesCommand(circuitBreaker,
                    okHttpClient, true, "http://localhost:9393", "user-003");
            GetAllocatedFeaturesResponse actual = cmd.execute();
            actualResults[i] = actual.getFeatures().size();
            actualCircuitClosed[i] = circuitBreaker.getState().equals(CircuitBreaker.State.CLOSED);
        }
        Assert.assertArrayEquals(expectedResult, actualResults);
        Assert.assertArrayEquals(expectedCircuitClosed, actualCircuitClosed);
    }

    @Test
    public void GetAllocatedFeatureNamesCommand_CircuitBreakerDisabled() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"features\":[{\"feature_id\":\"053e06de-a3d5-4b19-8ff5-346fb3aed8d7\"," +
                                "\"feature_name\":\"www-quick-checkout-navbar-v2\"},{\"feature_id\":" +
                                "\"06ea2c76-f6c0-4500-964e-4920e902f11b\",\"feature_name\":\"jenius-callback\"}]}}")
                        .withFixedDelay(2000)
                ));

        CircuitBreaker circuitBreaker = provideCircuitBreaker();
        OkHttpClient okHttpClient = provideOkHttpClient();
        int[] actualResults = new int[8];
        int[] expectedResult = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 8; i++) {
            GetAllocatedFeatureNamesCommand cmd = new GetAllocatedFeatureNamesCommand(circuitBreaker, okHttpClient,
                    false, "http://localhost:9393", "user-003");
            GetAllocatedFeaturesResponse actual = cmd.execute();
            actualResults[i] = actual.getFeatures().size();
        }
        Assert.assertArrayEquals(expectedResult, actualResults);
    }

    private CircuitBreaker provideCircuitBreaker() {
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
        return circuitBreakerRegistry
                .circuitBreaker("kaleApiFeatureflagTest");
    }

    private OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MILLISECONDS)
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .writeTimeout(500, TimeUnit.MILLISECONDS)
                .build();
    }
}
