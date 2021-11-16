package com.sayurbox.kale.abtest.command;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.config.KaleHystrixParams;
import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

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
                provideHystrixParams(500),
                new OkHttpClient(),
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
                provideHystrixParams(1_000),
                new OkHttpClient(),
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
                provideHystrixParams(1_000),
                new OkHttpClient(),
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

    private KaleHystrixParams provideHystrixParams(Integer timeout) {
        return new KaleHystrixParams(
                timeout,
                500,
                20,
                100,
                100
        );
    }
}