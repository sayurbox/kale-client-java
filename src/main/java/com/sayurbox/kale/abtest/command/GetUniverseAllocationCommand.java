package com.sayurbox.kale.abtest.command;

import com.google.gson.reflect.TypeToken;
import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;
import com.sayurbox.kale.common.client.DataResponse;
import com.sayurbox.kale.common.command.KaleCommand;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class GetUniverseAllocationCommand extends KaleCommand<GetUniverseAllocationResponse> {

    private final String userId;
    private final String universeId;

    private static final String ENDPOINT = "%s/v1/abtest/allocation/%s/%s";

    public GetUniverseAllocationCommand(
            CircuitBreaker circuitBreaker,
            OkHttpClient okHttpClient,
            boolean isCircuitBreakerEnabled,
            String baseUrl,
            String userId,
            String universeId
    ) {
        super(circuitBreaker, okHttpClient, isCircuitBreakerEnabled, baseUrl);
        this.userId = userId;
        this.universeId = universeId;
    }

    @Override
    public GetUniverseAllocationResponse getFallback() {
        return null;
    }

    @Override
    protected Request createRequest() {
        String url = String.format(ENDPOINT, baseUrl, userId, universeId);
        return new Request.Builder().post(RequestBody.create(null, new byte[]{})).url(url).build();
    }

    protected GetUniverseAllocationResponse handleResponse(Response response) throws IOException {
        String body = response.body().string();
        if (!response.isSuccessful()) {
            return getFallback();
        }
        DataResponse<GetUniverseAllocationResponse> t = gson.fromJson(body,
                new TypeToken<DataResponse<GetUniverseAllocationResponse>>() {}.getType());
        return t.getData();
    }
}
