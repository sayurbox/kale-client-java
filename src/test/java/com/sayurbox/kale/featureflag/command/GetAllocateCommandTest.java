package com.sayurbox.kale.featureflag.command;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sayurbox.kale.featureflag.client.GetAllocateResponse;
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

public class GetAllocateCommandTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9393);

    @Before
    public void before() {
        WireMock.reset();
    }

    @Test
    public void GetAllocateCommand_TimeoutRequest_ShouldFallbackFalse() {
        stubFor(get(urlEqualTo("/v1/featureflag/allocation/user-003/feature-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":true}}")
                        .withFixedDelay(1000)
                ));
        GetAllocateCommand cmd = new GetAllocateCommand(provideCircuitBreaker(), provideOkHttpClient(),
                true, "http://localhost:9393", "user-003", "feature-003");
        GetAllocateResponse actual = cmd.execute();
        Assert.assertNotNull(actual);
        Assert.assertFalse(actual.getRollout());
    }

    @Test
    public void GetAllocateCommand_Non200Response() {
        stubFor(get(urlEqualTo("/v1/featureflag/allocation/user-001/feature-001"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"code\":1234,\"message\":\"invalid data\",\"type\":\"Error\"}}")
                ));
        GetAllocateCommand cmd = new GetAllocateCommand(provideCircuitBreaker(), provideOkHttpClient(),
                true, "http://localhost:9393", "user-001", "feature-001");

        GetAllocateResponse actual = cmd.execute();
        Assert.assertNotNull(actual);
        Assert.assertFalse(actual.getRollout());
    }

    @Test
    public void GetAllocateCommand_AllocateTrue() {
        stubFor(get(urlEqualTo("/v1/featureflag/allocation/user-001/feature-001"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":true}}")
                ));
        GetAllocateCommand cmd = new GetAllocateCommand(provideCircuitBreaker(), provideOkHttpClient(),
                true, "http://localhost:9393", "user-001", "feature-001");
        GetAllocateResponse actual = cmd.execute();
        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.getRollout());
    }

    @Test
    public void GetAllocateCommand_AllocateFalse() throws IOException {
        stubFor(get(urlEqualTo("/v1/featureflag/allocation/user-004/feature-004"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":false}}")
                ));
        GetAllocateCommand cmd = new GetAllocateCommand(provideCircuitBreaker(), provideOkHttpClient(),
                true, "http://localhost:9393", "user-004", "feature-004");
        GetAllocateResponse actual = cmd.execute();
        Assert.assertNotNull(actual);
        Assert.assertFalse(actual.getRollout());
    }

    @Test
    public void GetAllocateCommand_CircuitBreakerClosed_ShouldFallbackFalse() {
        stubFor(get(urlEqualTo("/v1/featureflag/allocation/user-003/feature-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":true}}")
                        .withFixedDelay(200)
                ));
        CircuitBreaker circuitBreaker = provideCircuitBreaker();
        OkHttpClient okHttpClient = provideOkHttpClient();
        boolean[] actualResults = new boolean[10];
        boolean[] expectedResult = new boolean[] {true, true, true, true, true, false, false, false, false, false};
        for (int i = 0; i < 10; i++) {
            GetAllocateCommand cmd = new GetAllocateCommand(circuitBreaker, okHttpClient,
                    true, "http://localhost:9393", "user-003", "feature-003");
            GetAllocateResponse actual = cmd.execute();
            actualResults[i] = actual.getRollout();
        }
        Assert.assertArrayEquals(expectedResult, actualResults);
    }

    @Test
    public void GetAllocateCommand_CircuitBreakerDisabled() {
        stubFor(get(urlEqualTo("/v1/featureflag/allocation/user-003/feature-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":true}}")
                        .withFixedDelay(2000)
                ));
        CircuitBreaker circuitBreaker = provideCircuitBreaker();
        OkHttpClient okHttpClient = provideOkHttpClient();
        boolean[] actualResults = new boolean[8];
        boolean[] expectedResult = new boolean[] {false, false, false, false, false, false, false, false};
        for (int i = 0; i < 8; i++) {
            GetAllocateCommand cmd = new GetAllocateCommand(circuitBreaker, okHttpClient,
                    false, "http://localhost:9393", "user-003", "feature-003");
            GetAllocateResponse actual = cmd.execute();
            actualResults[i] = actual.getRollout();
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
