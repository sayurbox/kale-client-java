package com.sayurbox.kale.featureflag.command;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;

public class GetAllocateCommandTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9393);

    @Before
    public void before() {
        WireMock.reset();
    }
/*
    @Test
    public void GetAllocateCommand_TimeoutRequest_ShouldFallbackFalse() {
        stubFor(get(urlEqualTo("/v1/featureflag/allocation/user-003/feature-003"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\":{\"rollout\":true}}")
                        .withFixedDelay(5000)
                ));
        GetAllocateCommand cmd = new GetAllocateCommand(provideHystrixParams(500), new OkHttpClient(),
                "http://localhost:9393", "user-003", "feature-003");
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
        GetAllocateCommand cmd = new GetAllocateCommand(provideHystrixParams(1_000), new OkHttpClient(),
                "http://localhost:9393", "user-001", "feature-001");
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
        GetAllocateCommand cmd = new GetAllocateCommand(provideHystrixParams(1_000), new OkHttpClient(),
                "http://localhost:9393", "user-001", "feature-001");
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
        GetAllocateCommand cmd = new GetAllocateCommand(provideHystrixParams(1_000), new OkHttpClient(),
                "http://localhost:9393", "user-004", "feature-004");
        GetAllocateResponse actual = cmd.execute();
        Assert.assertNotNull(actual);
        Assert.assertFalse(actual.getRollout());
    }

    private KaleHystrixParams provideHystrixParams(Integer timeout) {
        return new KaleHystrixParams(timeout, 500, 20, 100, 100);
    }
*/
}
