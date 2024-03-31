package com.sayurbox.kale.featureflag;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sayurbox.kale.config.KaleConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class FeatureFlagClientImplTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9494);

    private FeatureFlagClient featureFlagClient;

    @Before
    public void before() {
        WireMock.reset();
        KaleConfig cfg = new KaleConfig.Builder()
                .withBaseUrl("http://localhost:9494")
                .withLoggerEnabled(true)
                .build();
        featureFlagClient = new FeatureFlagClientImpl(cfg);
    }

    @Test
    public void isAllocate_True() {
        stubFor(get(urlEqualTo("/v1/featureflag/allocation/user-003/feature-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":true}}")
                ));
        boolean actual = featureFlagClient.isAllocate("feature-003", "user-003");
        Assert.assertTrue(actual);
    }

    @Test
    public void isAllocateV2_True() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-003/feature-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":true}}")
                ));
        boolean actual = featureFlagClient.isAllocateV2("feature-003", "user-003");
        Assert.assertTrue(actual);
    }

    @Test
    public void isAllocate_False() {
        stubFor(get(urlEqualTo("/v1/featureflag/allocation/user-004/feature-004"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":false}}")
                ));
        boolean actual = featureFlagClient.isAllocate("feature-004", "user-004");
        Assert.assertFalse(actual);
    }

    @Test
    public void isAllocateV2_False() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-004/feature-004"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":false}}")
                ));
        boolean actual = featureFlagClient.isAllocateV2("feature-004", "user-004");
        Assert.assertFalse(actual);
    }

    @Test
    public void isAllocate_HasErrorResponse() {
        stubFor(get(urlEqualTo("/v1/featureflag/allocation/user-005/feature-005"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"code\":1234,\"message\":\"just error\",\"type\":\"TestError\"}}")
                ));
        boolean actual = featureFlagClient.isAllocate("feature-005", "user-005");
        Assert.assertFalse(actual);
    }

    @Test
    public void isAllocateV2_HasErrorResponse() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-005/feature-005"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"code\":1234,\"message\":\"just error\",\"type\":\"TestError\"}}")
                ));
        boolean actual = featureFlagClient.isAllocateV2("feature-005", "user-005");
        Assert.assertFalse(actual);
    }

    @Test
    public void getAllocatedFeatureNames_HasList() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"features\":[{\"feature_id\":\"053e06de-a3d5-4b19-8ff5-346fb3aed8d7\"," +
                                "\"feature_name\":\"www-quick-checkout-navbar-v2\"}]}}")
                ));
        Set<String> actual = featureFlagClient.getAllocatedFeatureNames("user-003");
        Assert.assertFalse(actual.isEmpty());
    }

    @Test
    public void getAllocatedFeatureNames_NoList() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"features\":[]}}")
                ));
        Set<String> actual = featureFlagClient.getAllocatedFeatureNames("user-003");
        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void getAllocatedFeatureNames_HasErrorResponse() {
        stubFor(get(urlEqualTo("/v2/featureflag/allocation/user-003"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"code\":1234,\"message\":\"just error\",\"type\":\"TestError\"}}")
                ));
        Set<String> actual = featureFlagClient.getAllocatedFeatureNames("user-003");
        Assert.assertTrue(actual.isEmpty());
    }
}
