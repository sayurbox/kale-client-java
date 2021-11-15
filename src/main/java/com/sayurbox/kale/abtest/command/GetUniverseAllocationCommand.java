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

import java.io.UnsupportedEncodingException;

public class GetUniverseAllocationCommand extends KaleCommand<GetUniverseAllocationResponse> {

    private final String userId;
    private final String universeId;

    public GetUniverseAllocationCommand(
        KaleHystrixParams hystrixParams,
        OkHttpClient okHttpClient,
        String baseUrl,
        String userId,
        String universeId
    ) {
        super(hystrixParams, okHttpClient, baseUrl);
        this.userId = userId;
        this.universeId = universeId;
    }

    @Override
    public GetUniverseAllocationResponse getFallback() {
        GetUniverseAllocationResponse response = new GetUniverseAllocationResponse();
        return response;
    }

    @Override
    protected Request createRequest() throws UnsupportedEncodingException {
        String url = String.format("%s/v1/abtest/allocation/%s/%s",
                baseUrl, userId, universeId);
        return new Request.Builder().post(RequestBody.create(null, new byte[]{})).url(url).build();
    }

    protected GetUniverseAllocationResponse handleResponse(Response response) throws Exception {
        String body = response.body().string();
        if (!response.isSuccessful()) {
            throw new KaleException("Failed response from kale status: " +
                    response.code() + " body: " + response.body().string());
        }
        DataResponse<GetUniverseAllocationResponse> t = gson.fromJson(body,
               new TypeToken<DataResponse<GetUniverseAllocationResponse>>() {}.getType());
        return t.getData();
    }
}