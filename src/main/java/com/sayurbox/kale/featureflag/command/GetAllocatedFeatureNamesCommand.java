package com.sayurbox.kale.featureflag.command;

import com.google.gson.reflect.TypeToken;
import com.sayurbox.kale.common.client.DataResponse;
import com.sayurbox.kale.common.command.KaleCommand;
import com.sayurbox.kale.featureflag.client.GetAllocatedFeaturesResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

public class GetAllocatedFeatureNamesCommand extends KaleCommand<GetAllocatedFeaturesResponse> {

    private static final Logger logger = LoggerFactory.getLogger(GetAllocatedFeatureNamesCommand.class);

    private final String userId;

    public GetAllocatedFeatureNamesCommand(CircuitBreaker circuitBreaker,
                                           OkHttpClient okHttpClient,
                                           boolean isCircuitBreakerEnabled,
                                           String baseUrl,
                                           String userId) {
        super(circuitBreaker, okHttpClient, isCircuitBreakerEnabled, baseUrl);
        this.userId = userId;
    }

    @Override
    public GetAllocatedFeaturesResponse getFallback() {
        GetAllocatedFeaturesResponse response = new GetAllocatedFeaturesResponse();
        response.setFeatures(Collections.emptySet());
        return response;
    }

    @Override
    protected GetAllocatedFeaturesResponse handleResponse(Response response) throws IOException {
        if (response.body() == null) {
            logger.error("Failed response from kale status: {}", response.code());
            return getFallback();
        }

        String body = response.body().string();
        if (!response.isSuccessful()) {
            logger.error("Failed response from kale status: {} body: {}", response.code(), body);
            return getFallback();
        }

        DataResponse<GetAllocatedFeaturesResponse> t = this.gson.fromJson(body,
                new TypeToken<DataResponse<GetAllocatedFeaturesResponse>>() {}.getType());

        return t.getData();
    }

    @Override
    protected Request createRequest() {
        String url = String.format("%s/v2/featureflag/allocation/%s", baseUrl, userId);
        return new Request.Builder().get().url(url).build();
    }
}
