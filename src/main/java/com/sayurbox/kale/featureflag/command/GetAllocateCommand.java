package com.sayurbox.kale.featureflag.command;

import com.google.gson.reflect.TypeToken;
import com.sayurbox.kale.common.client.DataResponse;
import com.sayurbox.kale.common.command.KaleCommand;
import com.sayurbox.kale.config.KaleHystrixParams;
import com.sayurbox.kale.exception.KaleException;
import com.sayurbox.kale.featureflag.client.GetAllocateResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.UnsupportedEncodingException;

public class GetAllocateCommand extends KaleCommand<GetAllocateResponse> {

    private final String userId;
    private final String featureId;

    public GetAllocateCommand(KaleHystrixParams hystrixParams,
                              OkHttpClient okHttpClient,
                              String baseUrl,
                              String userId,
                              String featureId) {
        super(hystrixParams, okHttpClient, baseUrl);
        this.userId = userId;
        this.featureId = featureId;
    }

    @Override
    public GetAllocateResponse getFallback() {
        GetAllocateResponse response = new GetAllocateResponse();
        response.setRollout(false);
        return response;
    }

    @Override
    protected Request createRequest() throws UnsupportedEncodingException {
        String url = String.format("%s/v1/featureflag/allocation/%s/%s",
                baseUrl, userId, featureId);
        return new Request.Builder().get().url(url).build();
    }

    protected GetAllocateResponse handleResponse(Response response) throws Exception {
        String body = response.body().string();
        if (!response.isSuccessful()) {
            throw new KaleException("Failed response from kale status: " +
                    response.code() + " body: " + response.body().string());
        }
        DataResponse<GetAllocateResponse> t = gson.fromJson(body,
               new TypeToken<DataResponse<GetAllocateResponse>>() {}.getType());
        return t.getData();
    }
}
