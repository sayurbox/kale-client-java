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
import java.util.Collections;
import java.util.List;

public class GetAllUniverseAllocationsCommand extends KaleCommand<List<GetUniverseAllocationResponse>> {

    private final String userId;

    private static final String ENDPOINT = "%s/v1/abtest/allocation/%s";

    public GetAllUniverseAllocationsCommand(
            CircuitBreaker circuitBreaker,
            OkHttpClient okHttpClient,
            boolean isCircuitBreakerEnabled,
            String baseUrl,
            String userId
    ) {
        super(circuitBreaker, okHttpClient, isCircuitBreakerEnabled, baseUrl);
        this.userId = userId;
    }

    @Override
    public List<GetUniverseAllocationResponse> getFallback() {
        return Collections.emptyList();
    }

    @Override
    protected Request createRequest() {
        String url = String.format(ENDPOINT, baseUrl, userId);
        return new Request.Builder().post(RequestBody.create(null, new byte[]{})).url(url).build();
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
