package com.sayurbox.kale.abtest;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.config.KaleConfig;
import org.junit.*;

import java.util.HashMap;
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

    @Ignore("Real test on staging")
    @Test
    public void getUniverseAllocationRealTest_Success() {
        KaleConfig cfg = new KaleConfig.Builder().withBaseUrl("https://kale-api.sayurbox.tech")
                .build();
        abTestClient = new ABTestClientImpl(cfg);

        HashMap<String, String> properties = new HashMap<>();
        properties.put("wh_code", "JK_01");

        List<GetUniverseAllocationResponse> allocationsResponse = abTestClient.getAllUniverseAllocations("user-003",properties);
        Assert.assertNotEquals(0, allocationsResponse.size());

        allocationsResponse.stream().filter(a -> a.getUniverseId().equals("cd6f7e9e-4abe-41c3-bd10-3afbd14afdb2")).forEach(a -> {
            Assert.assertEquals("user-003", a.getUserId());
            Assert.assertEquals("cd6f7e9e-4abe-41c3-bd10-3afbd14afdb2", a.getUniverseId());
            Assert.assertEquals("f4f56090-5a1d-49d6-899b-1ce5178f7d5d", a.getExperimentId());
        });

        // Test without properties
        allocationsResponse = abTestClient.getAllUniverseAllocations("user-003");
        Assert.assertNotEquals(0, allocationsResponse.size());

        allocationsResponse.stream().filter(a -> a.getUniverseId().equals("cd6f7e9e-4abe-41c3-bd10-3afbd14afdb2")).forEach(a -> {
            Assert.assertEquals("user-003", a.getUserId());
            Assert.assertEquals("cd6f7e9e-4abe-41c3-bd10-3afbd14afdb2", a.getUniverseId());
            Assert.assertEquals("f4f56090-5a1d-49d6-899b-1ce5178f7d5d", a.getExperimentId());
        });
    }
}
