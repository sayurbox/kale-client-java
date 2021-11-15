package com.sayurbox.kale.abtest.command;

import com.google.gson.reflect.TypeToken;
import com.sayurbox.kale.common.client.DataResponse;
import com.sayurbox.kale.common.command.KaleCommand;
import com.sayurbox.kale.config.KaleHystrixParams;
import com.sayurbox.kale.exception.KaleException;
import com.sayurbox.kale.abtest.client.GetUniverseAllocationResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.Collections;
import java.util.List;

public class GetAllUniverseAllocationsCommand extends KaleCommand<List<GetUniverseAllocationResponse>> {

    private final String userId;

    public GetAllUniverseAllocationsCommand(KaleHystrixParams hystrixParams,
        OkHttpClient okHttpClient,
        String baseUrl,
        String userId
    ) {
        super(hystrixParams, okHttpClient, baseUrl);
        this.userId = userId;
    }

    @Override
    public List<GetUniverseAllocationResponse> getFallback() {
        return Collections.emptyList();
    }

    @Override
    protected Request createRequest() {
        String url = String.format("%s/v1/abtest/allocation/%s",
                baseUrl, userId);
        return new Request.Builder().post(RequestBody.create(null, new byte[]{})).url(url).build();
    }

    protected List<GetUniverseAllocationResponse> handleResponse(Response response) throws Exception {
        String body = response.body().string();
        if (!response.isSuccessful()) {
            throw new KaleException("Failed response from kale status: " +
                    response.code() + " body: " + response.body().string());
        }
        DataResponse<List<GetUniverseAllocationResponse>> t = gson.fromJson(body,
               new TypeToken<DataResponse<List<GetUniverseAllocationResponse>>>() {}.getType());
        return t.getData();
    }
}
