package com.sayurbox.kale.abtest.command;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class GetUniverseAllocationByNameCommandTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9393);

    @Before
    public void before() {
        WireMock.reset();
    }

    @Test
    public void GetUniverseAllocationByNameCommand_TimeoutRequest_ShouldFallback() {
        stubFor(post(urlEqualTo("/v2/abtest/allocation/user-003/universe-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"user_id\":\"user-003\",\"universe_id\":\"universe-003\"," +
                                "\"experiment_id\":\"experiment-003\",\"variant_id\":\"variant-003\"," +
                                "\"configs\":[{\"key\":\"color\",\"value\":\"red\"}]}}")
                        .withFixedDelay(5000)
                ));
        GetUniverseAllocationByNameCommand cmd = new GetUniverseAllocationByNameCommand(
                provideCircuitBreaker(),
                provideOkHttpClient(),
                true,
                "http://localhost:9393",
                "user-003",
                "universe-003",
                null
        );
        GetUniverseAllocationResponse actual = cmd.execute();

        Assert.assertNull(actual);
    }

    @Test
    public void GetUniverseAllocationByNameCommand_Non200Response() {
        stubFor(post(urlEqualTo("/v2/abtest/allocation/user-003/universe-003"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"code\":1234,\"message\":\"invalid data\",\"type\":\"Error\"}}")
                ));
        GetUniverseAllocationByNameCommand cmd = new GetUniverseAllocationByNameCommand(
                provideCircuitBreaker(),
                provideOkHttpClient(),
                true,
                "http://localhost:9393",
                "user-003",
                "universe-003",
                null
        );
        GetUniverseAllocationResponse actual = cmd.execute();

        Assert.assertNull(actual);
    }

    @Test
    public void GetUniverseAllocationByNameCommand_Success() {
        stubFor(post(urlEqualTo("/v2/abtest/allocation/user-003/universe-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"user_id\":\"user-003\",\"universe_id\":\"universe-003\"," +
                                "\"experiment_id\":\"experiment-003\",\"variant_id\":\"variant-003\"," +
                                "\"configs\":[{\"key\":\"color\",\"value\":\"red\"}]}}")
                ));
        GetUniverseAllocationByNameCommand cmd = new GetUniverseAllocationByNameCommand(
                provideCircuitBreaker(),
                provideOkHttpClient(),
                true,
                "http://localhost:9393",
                "user-003",
                "universe-003",
                null
        );
        GetUniverseAllocationResponse actual = cmd.execute();
        Assert.assertEquals("user-003", actual.getUserId());
        Assert.assertEquals("universe-003", actual.getUniverseId());
        Assert.assertEquals("experiment-003", actual.getExperimentId());
        Assert.assertEquals("variant-003", actual.getVariantId());
        Assert.assertEquals("color", actual.getConfigs().get(0).get("key"));
        Assert.assertEquals("red", actual.getConfigs().get(0).get("value"));
    }

    @Test
    public void GetUniverseAllocationWithPropertiesCommand_Success() {
        MappingBuilder mappingBuilder = post(urlEqualTo("/v2/abtest/allocation/user-003/universe-003"))
                .withRequestBody(WireMock.equalToJson("{\"wh_code\":\"JK01\"}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"user_id\":\"user-003\",\"universe_id\":\"universe-003\"," +
                                "\"experiment_id\":\"experiment-003\",\"variant_id\":\"variant-003\"," +
                                "\"configs\":[{\"key\":\"color\",\"value\":\"red\"}]}}")
                );

        stubFor(mappingBuilder);
        HashMap<String, String> properties = new HashMap<String, String>(1) {{
            put("wh_code", "JK01");
        }};
        GetUniverseAllocationByNameCommand cmd = new GetUniverseAllocationByNameCommand(
                provideCircuitBreaker(),
                provideOkHttpClient(),
                true,
                "http://localhost:9393",
                "user-003",
                "universe-003",
                properties
        );
        GetUniverseAllocationResponse actual = cmd.execute();
        Assert.assertEquals("user-003", actual.getUserId());
        Assert.assertEquals("universe-003", actual.getUniverseId());
        Assert.assertEquals("experiment-003", actual.getExperimentId());
        Assert.assertEquals("variant-003", actual.getVariantId());
        Assert.assertEquals("color", actual.getConfigs().get(0).get("key"));
        Assert.assertEquals("red", actual.getConfigs().get(0).get("value"));
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