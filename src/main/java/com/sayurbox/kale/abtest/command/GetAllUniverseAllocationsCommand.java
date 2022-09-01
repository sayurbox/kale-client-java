package com.sayurbox.kale.abtest.command;

import com.google.gson.reflect.TypeToken;
import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.common.client.DataResponse;
import com.sayurbox.kale.common.command.KaleCommand;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllUniverseAllocationsCommand extends KaleCommand<List<GetUniverseAllocationResponse>> {

    private final String userId;
    private final Map<String, String> properties;
    Type propertiesType = new TypeToken<HashMap<String, String>>(){}.getType();

    private static final String ENDPOINT = "%s/v1/abtest/allocation/%s";

    public GetAllUniverseAllocationsCommand(
            CircuitBreaker circuitBreaker,
            OkHttpClient okHttpClient,
            boolean isCircuitBreakerEnabled,
            String baseUrl,
            String userId,
            Map<String, String> properties
    ) {
        super(circuitBreaker, okHttpClient, isCircuitBreakerEnabled, baseUrl);
        this.userId = userId;
        this.properties = properties;
    }

    @Override
    public List<GetUniverseAllocationResponse> getFallback() {
        return Collections.emptyList();
    }

    @Override
    protected Request createRequest() {
        String url = String.format(ENDPOINT, baseUrl, userId);
        byte[] body = gson.toJson(properties, propertiesType).getBytes();
        MediaType contentType = MediaType.parse("application/json");

        return new Request.Builder().post(RequestBody.create(contentType, body)).url(url).build();
    }

    protected List<GetUniverseAllocationResponse> handleResponse(Response response) throws IOException {
        String body = response.body().string();
        if (!response.isSuccessful()) {
            return getFallback();
        }
        DataResponse<List<GetUniverseAllocationResponse>> t = gson.fromJson(body,
               new TypeToken<DataResponse<List<GetUniverseAllocationResponse>>>() {}.getType());
        return t.getData();
    }
}
