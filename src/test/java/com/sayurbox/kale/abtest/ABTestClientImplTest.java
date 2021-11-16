package com.sayurbox.kale.abtest;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.config.KaleConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class ABTestClientImplTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9494);

    private ABTestClient abTestClient;

    @Before
    public void before() {
        WireMock.reset();
        KaleConfig cfg = new KaleConfig.Builder().withBaseUrl("http://localhost:9494")
                .build();
        abTestClient = new ABTestClientImpl(cfg);
    }

    @Test
    public void getUniverseAllocation_Success() {
        stubFor(post(urlEqualTo("/v1/abtest/allocation/user-003/universe-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"user_id\":\"user-003\",\"universe_id\":\"universe-003\"," +
                                "\"experiment_id\":\"experiment-003\",\"variant_id\":\"variant-003\"," +
                                "\"configs\":[{\"key\":\"color\",\"value\":\"red\"}]}}")
                ));
        GetUniverseAllocationResponse actual = abTestClient.getUniverseAllocation(
                "user-003", "universe-003"
        );

        Assert.assertEquals("user-003", actual.getUserId());
        Assert.assertEquals("universe-003", actual.getUniverseId());
        Assert.assertEquals("experiment-003", actual.getExperimentId());
        Assert.assertEquals("variant-003", actual.getVariantId());
        Assert.assertEquals("color", actual.getConfigs().get(0).get("key"));
        Assert.assertEquals("red", actual.getConfigs().get(0).get("value"));
    }

    @Test
    public void getUniverseAllocation_HasErrorResponse() {
        stubFor(post(urlEqualTo("/v1/abtest/allocation/user-003/universe-003"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"code\":1234,\"message\":\"just error\",\"type\":\"TestError\"}}")
                ));
        GetUniverseAllocationResponse actual = abTestClient.getUniverseAllocation(
                "user-003", "universe-003"
        );

        Assert.assertNull(actual);
    }

    @Test
    public void getAllUniverseAllocations_Success() {
        stubFor(post(urlEqualTo("/v1/abtest/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":[{\"user_id\":\"user-003\",\"universe_id\":\"universe-003\"," +
                                "\"experiment_id\":\"experiment-003\",\"variant_id\":\"variant-003\"," +
                                "\"configs\":[{\"key\":\"color\",\"value\":\"red\"}]}]}")
                ));
        List<GetUniverseAllocationResponse> actual = abTestClient.getAllUniverseAllocations("user-003");
        GetUniverseAllocationResponse alloc = actual.get(0);

        Assert.assertEquals("user-003", alloc.getUserId());
        Assert.assertEquals("universe-003", alloc.getUniverseId());
        Assert.assertEquals("experiment-003", alloc.getExperimentId());
        Assert.assertEquals("variant-003", alloc.getVariantId());
        Assert.assertEquals("color", alloc.getConfigs().get(0).get("key"));
        Assert.assertEquals("red", alloc.getConfigs().get(0).get("value"));
    }

    @Test
    public void getAllUniverseAllocations_HasErrorResponse() {
        stubFor(post(urlEqualTo("/v1/abtest/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"code\":1234,\"message\":\"just error\",\"type\":\"TestError\"}}")
                ));
        List<GetUniverseAllocationResponse> actual = abTestClient.getAllUniverseAllocations("user-003");

        Assert.assertNotNull(actual);
        Assert.assertEquals(0, actual.size());
    }
}