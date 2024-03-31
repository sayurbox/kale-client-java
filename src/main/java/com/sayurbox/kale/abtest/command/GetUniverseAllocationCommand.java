package com.sayurbox.kale.abtest.command;

import com.google.gson.reflect.TypeToken;
import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.common.client.DataResponse;
import com.sayurbox.kale.common.command.KaleCommand;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GetUniverseAllocationCommand extends KaleCommand<GetUniverseAllocationResponse> {

    private final String userId;
    private final String universeId;
    private final Map<String, String> properties;
    Type propertiesType = new TypeToken<HashMap<String, String>>() {
    }.getType();

    private static final String ENDPOINT = "%s/v1/abtest/allocation/%s/%s";

    public GetUniverseAllocationCommand(
            CircuitBreaker circuitBreaker,
            OkHttpClient okHttpClient,
            boolean isCircuitBreakerEnabled,
            String baseUrl,
            String userId,
            String universeId,
            Map<String, String> properties
    ) {
        super(circuitBreaker, okHttpClient, isCircuitBreakerEnabled, baseUrl);
        this.userId = userId;
        this.universeId = universeId;
        this.properties = properties;
    }

    @Override
    public GetUniverseAllocationResponse getFallback() {
        return null;
    }

    @Override
    protected Request createRequest() {
        String url = String.format(ENDPOINT, baseUrl, userId, universeId);
        MediaType contentType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(gson.toJson(properties, propertiesType), contentType);

        return new Request.Builder().post(body).url(url).build();
    }

    protected GetUniverseAllocationResponse handleResponse(Response response) throws IOException {
        if (response.body() == null || !response.isSuccessful()) {
            return getFallback();
        }

        String body = response.body().string();
        DataResponse<GetUniverseAllocationResponse> t = gson.fromJson(body,
                new TypeToken<DataResponse<GetUniverseAllocationResponse>>() {
                }.getType());
        return t.getData();
    }
}
