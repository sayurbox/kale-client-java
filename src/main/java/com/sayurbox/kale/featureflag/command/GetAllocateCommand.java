package com.sayurbox.kale.featureflag.command;

import com.google.gson.reflect.TypeToken;
import com.sayurbox.kale.common.client.DataResponse;
import com.sayurbox.kale.common.command.KaleCommand;
import com.sayurbox.kale.featureflag.client.GetAllocateResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GetAllocateCommand extends KaleCommand<GetAllocateResponse> {

    private static final Logger logger = LoggerFactory.getLogger(GetAllocateCommand.class);

    private final String baseUrl;
    private final String userId;
    private final String featureId;

    public GetAllocateCommand(CircuitBreaker circuitBreaker,
                              OkHttpClient okHttpClient,
                              boolean isCircuitBreakerEnabled,
                              String baseUrl,
                              String userId,
                              String featureId) {
        super(circuitBreaker, okHttpClient, isCircuitBreakerEnabled, baseUrl);
        this.userId = userId;
        this.featureId = featureId;
        this.baseUrl = baseUrl;
    }

    @Override
    public GetAllocateResponse getFallback() {
        GetAllocateResponse response = new GetAllocateResponse();
        response.setRollout(false);
        return response;
    }

    @Override
    protected Request createRequest() {
        String url = String.format("%s/v1/featureflag/allocation/%s/%s",
                baseUrl, userId, featureId);
        return new Request.Builder().get().url(url).build();
    }

    protected GetAllocateResponse handleResponse(Response response) throws IOException {
        String body = response.body().string();
        if (!response.isSuccessful()) {
            logger.error("Failed response from kale status: {} body: {}", response.code(), body);
            return getFallback();
        }
        DataResponse<GetAllocateResponse> t = this.gson.fromJson(body,
               new TypeToken<DataResponse<GetAllocateResponse>>() {}.getType());
        return t.getData();
    }
}
