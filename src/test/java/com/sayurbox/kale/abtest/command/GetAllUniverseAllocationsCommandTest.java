package com.sayurbox.kale.abtest.command;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class GetAllUniverseAllocationsCommandTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9393);

    @Before
    public void before() {
        WireMock.reset();
    }

    @Test
    public void GetAllUniverseAllocationsCommand_TimeoutRequest_ShouldFallback() {
        stubFor(post(urlEqualTo("/v1/abtest/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":[{\"user_id\":\"user-003\",\"universe_id\":\"universe-003\"," +
                                "\"experiment_id\":\"experiment-003\",\"variant_id\":\"variant-003\"" +
                                "\"configs\":[{\"key\":\"color\",\"value\":\"red\"}]}]}")
                        .withFixedDelay(5000)
                ));
        GetAllUniverseAllocationsCommand cmd = new GetAllUniverseAllocationsCommand(
                provideCircuitBreaker(),
                provideOkHttpClient(),
                true,
                "http://localhost:9393",
                "user-003"
        );
        List<GetUniverseAllocationResponse> actual = cmd.execute();

        Assert.assertNotNull(actual);
        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void GetAllUniverseAllocationsCommand_Non200Response() {
        stubFor(post(urlEqualTo("/v1/abtest/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"code\":1234,\"message\":\"invalid data\",\"type\":\"Error\"}}")
                ));
        GetAllUniverseAllocationsCommand cmd = new GetAllUniverseAllocationsCommand(
                provideCircuitBreaker(),
                provideOkHttpClient(),
                true,
                "http://localhost:9393",
                "user-003"
        );
        List<GetUniverseAllocationResponse> actual = cmd.execute();

        Assert.assertNotNull(actual);
        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void GetAllUniverseAllocationsCommand_Success() {
        stubFor(post(urlEqualTo("/v1/abtest/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":[{\"user_id\":\"user-003\",\"universe_id\":\"universe-003\"," +
                                "\"experiment_id\":\"experiment-003\",\"variant_id\":\"variant-003\"," +
                                "\"configs\":[{\"key\":\"color\",\"value\":\"red\"}]}]}")
                ));
        GetAllUniverseAllocationsCommand cmd = new GetAllUniverseAllocationsCommand(
                provideCircuitBreaker(),
                provideOkHttpClient(),
                true,
                "http://localhost:9393",
                "user-003"
        );
        List<GetUniverseAllocationResponse> actual = cmd.execute();
        GetUniverseAllocationResponse alloc = actual.get(0);
        Assert.assertEquals("user-003", alloc.getUserId());
        Assert.assertEquals("universe-003", alloc.getUniverseId());
        Assert.assertEquals("experiment-003", alloc.getExperimentId());
        Assert.assertEquals("variant-003", alloc.getVariantId());
        Assert.assertEquals("color", alloc.getConfigs().get(0).get("key"));
        Assert.assertEquals("red", alloc.getConfigs().get(0).get("value"));
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
                .circuitBreaker("abTest");
    }

    private OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MILLISECONDS)
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .writeTimeout(500, TimeUnit.MILLISECONDS)
                .build();
    }

}